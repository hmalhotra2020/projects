package com.example.pumps.api;

import com.example.pumps.entity.DailyRevenue;
import com.example.pumps.store.CityRevenueRepository;
import com.example.pumps.store.DailyRevenueRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller that exposes aggregated analytics data consumed by the Chart.js dashboard.
 *
 * <p>All endpoints return JSON and are polled by the front-end every 5 seconds.
 */
@RestController
@RequestMapping("/api")
public class ChartsApiController {

    private final CityRevenueRepository cityRevenueRepository;
    private final DailyRevenueRepository dailyRevenueRepository;

    public ChartsApiController(CityRevenueRepository cityRevenueRepository,
                               DailyRevenueRepository dailyRevenueRepository) {
        this.cityRevenueRepository = cityRevenueRepository;
        this.dailyRevenueRepository = dailyRevenueRepository;
    }

    /**
     * Returns the top-10 cities by total revenue, ordered descending.
     *
     * <p>Response shape:
     * <pre>
     * [
     *   { "city": "Mumbai",    "totalAmt": 1250000 },
     *   { "city": "Delhi",     "totalAmt": 980000  },
     *   ...
     * ]
     * </pre>
     *
     * @return list of city-revenue maps
     */
    @GetMapping("/city-revenue")
    public List<Map<String, Object>> getCityRevenue() {
        return cityRevenueRepository.findTop10ByOrderByTotalAmtDesc()
                .stream()
                .map(cr -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("city", cr.getCity());
                    map.put("totalAmt", cr.getTotalAmt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns daily revenue aggregated by date, summing across hours and fuel types.
     *
     * <p>Response shape:
     * <pre>
     * {
     *   "dates": ["2024-01-14", "2024-01-15", ...],
     *   "qty":   [350, 420, ...],
     *   "amt":   [38500, 46200, ...]
     * }
     * </pre>
     * Dates are returned in chronological (ascending) order.
     *
     * @return map with {@code dates}, {@code qty}, and {@code amt} arrays
     */
    @GetMapping("/daily-revenue")
    public Map<String, List<?>> getDailyRevenue() {
        // Fetch up to 30 most recent records and group by date (ascending)
        List<DailyRevenue> records = dailyRevenueRepository.findTop30ByOrderByPdateDesc();

        // Aggregate: sum qty and amt per date
        Map<LocalDate, long[]> aggregated = new TreeMap<>(); // TreeMap keeps dates sorted
        for (DailyRevenue dr : records) {
            aggregated.compute(dr.getPdate(), (date, acc) -> {
                if (acc == null) acc = new long[]{0L, 0L};
                acc[0] += dr.getQty();
                acc[1] += dr.getAmt();
                return acc;
            });
        }

        List<String> dates = new ArrayList<>();
        List<Long> qtyList = new ArrayList<>();
        List<Long> amtList = new ArrayList<>();

        aggregated.forEach((date, acc) -> {
            dates.add(date.toString());
            qtyList.add(acc[0]);
            amtList.add(acc[1]);
        });

        Map<String, List<?>> result = new LinkedHashMap<>();
        result.put("dates", dates);
        result.put("qty", qtyList);
        result.put("amt", amtList);
        return result;
    }
}
