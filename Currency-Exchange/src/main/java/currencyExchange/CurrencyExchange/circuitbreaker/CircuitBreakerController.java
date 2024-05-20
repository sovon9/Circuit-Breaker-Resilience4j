package currencyExchange.CurrencyExchange.circuitbreaker;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {

	Logger logger = LogManager.getLogger(CircuitBreakerController.class);
	
	@Retry(name="p1-retry", fallbackMethod = "fallbackResponse")
	@GetMapping("/testcircuitbreak")
	public String getTestAPIRes()
	{
		logger.error("********	trying to connect to localhost:8888	*****************");
		ResponseEntity<String> res = new RestTemplate().getForEntity("http://localhost:8888", String.class);
		return res.getBody();
	}
	
	/**
	 *  fallback method accepts a parameter that extends throwable/ of type throwable
	 *  based on the exception we can send different response back
	 */
	public String fallbackResponse(ResourceAccessException exception)
	{
		return "**** Server not found or took too long to respond ****"+exception.getMessage();
	}
	
	public String fallbackResponse(RuntimeException exception)
	{
		return "**** RuntimeException occcured in Server ****"+exception.getMessage();
	}
	
	@RateLimiter(name = "r1-ratelimit")
	@Bulkhead(name = "r1-bulkhead")
	@GetMapping("/ratelimiter")
	public String getRateLimiterTest()
	{
		return "************* rate-limiter test **************";
	}
	
}
