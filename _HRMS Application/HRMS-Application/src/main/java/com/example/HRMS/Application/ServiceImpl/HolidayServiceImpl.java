package com.example.HRMS.Application.ServiceImpl;

import com.example.HRMS.Application.Entity.Holiday;
import com.example.HRMS.Application.Exception.ResourceNotFoundException;
import com.example.HRMS.Application.Repository.HolidayRepository;
import com.example.HRMS.Application.Service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public Holiday createHoliday(Holiday holiday) {
        try {
            validateHoliday(holiday);
            return holidayRepository.save(holiday);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid holiday data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create holiday: " + e.getMessage());
        }
    }

    @Override
    public List<Holiday> getAllHolidays() {
        try {
            return holidayRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch holidays: " + e.getMessage());
        }
    }

    @Override
    public Holiday getHolidayById(Long id) {
        try {
            return holidayRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with ID " + id));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get holiday: " + e.getMessage());
        }
    }

    @Override
    public Holiday updateHoliday(Long id, Holiday holiday) {
        try {
            validateHoliday(holiday);
            Holiday existing = getHolidayById(id);
            existing.setName(holiday.getName());
            existing.setDate(holiday.getDate());
            existing.setDescription(holiday.getDescription());
            return holidayRepository.save(existing);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid holiday update data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update holiday: " + e.getMessage());
        }
    }

    @Override
    public void deleteHoliday(Long id) {
        try {
            if (!holidayRepository.existsById(id)) {
                throw new ResourceNotFoundException("Holiday not found with ID " + id);
            }
            holidayRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete holiday: " + e.getMessage());
        }
    }

//    @Override
//    public Page<Holiday> getHolidaysPaginated(int page, int size, String sortBy, String direction) {
//        try {
//            Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//            Pageable pageable = PageRequest.of(page, size, sort);
//            return holidayRepository.findAll(pageable);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch paginated holidays: " + e.getMessage());
//        }
//    }

    private void validateHoliday(Holiday holiday) {
        if (holiday.getName() == null || holiday.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Holiday name must not be empty");
        }
        if (holiday.getDate() == null) {
            throw new IllegalArgumentException("Holiday date must not be null");
        }
    }
}
