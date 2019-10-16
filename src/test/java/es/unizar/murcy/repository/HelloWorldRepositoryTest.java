package es.unizar.murcy.repository;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.HelloWorld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
public class HelloWorldRepositoryTest {


        @Autowired
        private HelloWorldRepository helloWorldRepository;

        @Test
        public void givenHelloWorldRepository_whenSaveAndRetrieveEntity_thenOK() {
            HelloWorld genericHelloWorld = helloWorldRepository
                    .save(new HelloWorld("test"));
            Optional<HelloWorld> foundHelloWorld = helloWorldRepository
                    .findById(genericHelloWorld.getId());

            assertTrue(foundHelloWorld.isPresent());
            assertEquals(genericHelloWorld.getMessage(), foundHelloWorld.get().getMessage());
        }
}

