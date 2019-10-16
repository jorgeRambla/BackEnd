package es.unizar.murcy.service;

import es.unizar.murcy.model.HelloWorld;
import es.unizar.murcy.repository.HelloWorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HelloWorldService {

    @Autowired
    HelloWorldRepository helloWorldRepository;

    public List<HelloWorld> findAllHelloWorld() {
        return helloWorldRepository.findAll();
    }

    public Optional<HelloWorld> findHelloWorldById(long id) {
        return helloWorldRepository.findById(id);
    }

    public HelloWorld createHelloWorld(HelloWorld helloWorld) {
        return helloWorldRepository.save(helloWorld);
    }

    public HelloWorld updateHelloWorld(HelloWorld helloWorld) {
        return helloWorldRepository.save(helloWorld);
    }

    public void deleteHelloWorld(long helloWorldId) {
        helloWorldRepository.deleteById(helloWorldId);
    }
}
