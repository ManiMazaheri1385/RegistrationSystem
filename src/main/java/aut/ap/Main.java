package aut.ap;

import aut.ap.model.User;
import java.util.Scanner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static SessionFactory sessionFactory;

    private static void setUpSessionFactory() {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
    }

    private static void closeSessionFactory() {
        sessionFactory.close();
    }

    private static boolean signUp() {
        System.out.println("Enter your First Name: ");
        String firstName = scanner.nextLine();

        System.out.println("Enter your Last Name: ");
        String lastName = scanner.nextLine();

        System.out.println("Enter your Age: ");
        Integer age = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter your Email: ");
        String email = scanner.nextLine();

        System.out.println("Enter your Password: ");
        String password = scanner.nextLine();

        Session session = sessionFactory.openSession();

        try {
            Transaction tx = session.beginTransaction();
            User u = new User(firstName, lastName, age, email, password);
            session.persist(u);

            tx.commit();

            System.out.println("You have successfully registered!");
        } catch (ConstraintViolationException e) {
            System.out.println();
            System.out.println("An account with this email already exists");
            session.close();
            return false;
        } catch (Exception e) {
            System.out.println();
            System.out.println("Exception in the database: " + e.getMessage());
            session.close();
            return false;
        }
        session.close();
        return true;
    }

    private static boolean login() {
        System.out.println("Enter your Email: ");
        String email = scanner.nextLine();

        System.out.println("Enter your Password: ");
        String password = scanner.nextLine();

        Session session = sessionFactory.openSession();
        try {
            Transaction tx = session.beginTransaction();

            String hql = "FROM User WHERE email = :email";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("email", email);

            User u = query.uniqueResult();

            if (u == null) {
                System.out.println();
                System.out.println("Invalid email");
                return false;
            }

            if (!u.getPassword().equals(password)) {
                System.out.println();
                System.out.println("Wrong Password!");
                return false;
            }

            System.out.println();
            System.out.println("Welcome, " + u.getFirstName() + " " + u.getLastName() + "!");

            tx.commit();
        } catch (Exception e) {
            System.out.println();
            System.out.println("Exception in the database: " + e.getMessage());
            session.close();
            return false;
        }
        session.close();
        return true;
    }

    public static void main(String[] args) {
        setUpSessionFactory();

        while (true) {
            System.out.println();
            System.out.println("[L]ogin, [S]ign up, [Q]uit: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "L" -> {
                    try {
                        if (!login()) {
                            continue;
                        }
                    } catch (Exception e) {
                        System.out.println();
                        System.out.println("Exception in the database: " + e.getMessage());
                        continue;
                    }
                }
                case "S" -> {
                    try {
                        if (!signUp()) {
                            continue;
                        }
                    } catch (Exception e) {
                        System.out.println();
                        System.out.println("Exception in the database: " + e.getMessage());
                        continue;
                    }
                }
                case "Q" -> {}
                default -> {
                    System.out.println();
                    System.out.println("Invalid choice: " + choice);
                    continue;
                }
            }
            break;
        }

        closeSessionFactory();
    }
}
