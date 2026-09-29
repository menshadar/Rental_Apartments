package com.example.rentalapartments.service;

import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import com.example.rentalapartments.model.SearchLog;
import com.example.rentalapartments.repository.ApartmentRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService {

    private final ApartmentRepository repository;
    private final ObjectProvider<SearchLog> searchLogs;
    private ApartmentValidator validator;
    public ApartmentService(ApartmentRepository repository, ObjectProvider<SearchLog> searchLogs) {
        this.repository = repository;
        this.searchLogs = searchLogs;
    }

    @Autowired
    public void setValidator(ApartmentValidator validator) {
        this.validator = validator;
    }

    public List<Apartment> search(SearchCriteria c) {
        List<Apartment> result = repository.findAll().stream()
                .filter(a -> isEmpty(c.getCity()) || a.getCity().equalsIgnoreCase(c.getCity().trim()))
                .filter(a -> c.getRooms() == null || a.getRooms() == c.getRooms())
                .filter(a -> c.getMaxPrice() == null || a.getPrice() <= c.getMaxPrice())
                .filter(a -> c.getMinArea() == null || a.getArea() >= c.getMinArea())
                .toList();

        SearchLog log = searchLogs.getObject();
        log.print(c, result.size());
        return result;
    }

    public List<Apartment> findAll() { return repository.findAll(); }

    public Apartment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Квартиру не знайдено: " + id));
    }

    public Apartment save(Apartment apartment) {
        validator.validate(apartment);
        return repository.save(apartment);
    }

    public void delete(Long id) { repository.deleteById(id); }

    private boolean isEmpty(String s) { return s == null || s.isBlank(); }
}
