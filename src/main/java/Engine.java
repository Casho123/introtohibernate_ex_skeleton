import entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Select exercise number:");

        try {
            int exNum = Integer.parseInt(bufferedReader.readLine());

            switch (exNum) {
                case 2:
                    exerciseTwo();
                    break;
                case 3:
                    exerciseThree();
                    break;
                case 4:
                    exerciseFour();
                    break;
                case 5:
                    exerciseFive();
                    break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void exerciseFive() {
        System.out.println("Shows all employees from the Research and Development department:");

        entityManager.createQuery("SELECT e FROM Employee e WHERE e.department.name = :d_name ORDER by e.salary, e.id", Employee.class)
                .setParameter("d_name", "Research and Development")
                .getResultStream()
                .forEach(employee -> System.out.printf("%s %s from %s - $%.2f\n",
                        employee.getFirstName(), employee.getLastName(), employee.getDepartment().getName(), employee.getSalary()));


    }



    private void exerciseFour() {
        System.out.println("All employees with a salary over 50000:");
        List<Employee> employees = entityManager.createQuery("SELECT e FROM Employee e WHERE e.salary >= :min_salary", Employee.class)
                .setParameter("min_salary", BigDecimal.valueOf(50000L))
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(employee.getFirstName());

        }
    }

    private void exerciseThree() throws IOException {
        System.out.println("Enter full employee name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];


        Long result = entityManager.createQuery("SELECT count(e) FROM Employee e WHERE e.firstName = :f_name AND e.lastName = :l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName)
                .getSingleResult();

        System.out.println(result == 0 ? "NO" : "YES");


    }

    private void exerciseTwo() {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("UPDATE Town t SET t.name = upper(t.name) WHERE length(t.name) >= 5");
        int affectedRows = query.executeUpdate();
        System.out.println("Count of affected rows is: " + affectedRows);
        entityManager.getTransaction().commit();
        ;
    }
}
