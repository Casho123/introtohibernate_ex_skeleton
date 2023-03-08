import entities.Address;
import entities.Employee;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
                case 6:
                    exerciseSix();
                    break;
                case 7:
                    exerciseSeven();
                    break;
                case 10:
                    exerciseTen();
                    break;
                case 13:
                    exerciseThirteen();
                    break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void exerciseThirteen() throws IOException {
        System.out.println("Enter town name to delete:");
        String townName = bufferedReader.readLine();
        Town town = entityManager.createQuery("SELECT t FROM Town t WHERE t.name = :t_name", Town.class)
                .setParameter("t_name", townName)
                .getSingleResult();


        int affectedRows = removeTownById(town.getId());

        entityManager.getTransaction().begin();
        entityManager.remove(town);
        entityManager.getTransaction().commit();


        System.out.println(affectedRows == 1 ? String.format("1 address in %s removed", townName)
                : String.format("%d addresses in %s removed", affectedRows, townName));


    }

    private int removeTownById(Integer id) {
        List<Address> addresses = entityManager.createQuery("SELECT a FROM Address a WHERE a.town.id = :p_id", Address.class)
                .setParameter("p_id", id)
                .getResultList();

        entityManager.getTransaction().begin();

        addresses.forEach(entityManager::remove);

        entityManager.getTransaction().commit();
        return addresses.size();
    }


    private void exerciseTen() {

        entityManager.getTransaction().begin();
        ;

        int affectedRows = entityManager.createQuery("UPDATE Employee e SET e.salary = e.salary * 1.2 WHERE e.department.id IN :ids")
                .setParameter("ids", Set.of(1, 2, 4, 11))
                .executeUpdate();
        System.out.println(affectedRows);

        entityManager.getTransaction().commit();

    }

    private void exerciseSeven() {
        List<Address> addresses = entityManager.createQuery("SELECT a FROM Address a ORDER by a.employees.size DESC", Address.class)
                .setMaxResults(10)
                .getResultList();

        addresses.forEach(address -> {
            System.out.printf("%s , %s - %d employees\n",
                    address.getText(),
                    address.getTown() == null ? "Unknown" : address.getTown().getName(),
                    address.getEmployees().size());
        });


    }

    private void exerciseSix() throws IOException {
        System.out.println("Enter employee last name:");
        String lastName = bufferedReader.readLine();

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e WHERE e.lastName = :l_name", Employee.class)
                .setParameter("l_name", lastName)
                .getSingleResult();

        Address address = createAddress("Vitoshka 15");

        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();

    }

    private Address createAddress(String addressText) {
        Address address = new Address();
        address.setText(addressText);

        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();

        return address;
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
