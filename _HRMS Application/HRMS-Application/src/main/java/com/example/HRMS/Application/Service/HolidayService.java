package com.example.HRMS.Application.Service;


import com.example.HRMS.Application.Entity.Holiday;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HolidayService {

    Holiday createHoliday(Holiday holiday);

    List<Holiday> getAllHolidays();

    Holiday getHolidayById(Long id);

    Holiday updateHoliday(Long id, Holiday holiday);

    void deleteHoliday(Long id);

    //Page<Holiday> getHolidaysPaginated(int page, int size, String sortBy, String direction);
}
