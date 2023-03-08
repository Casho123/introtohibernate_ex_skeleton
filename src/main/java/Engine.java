import entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
