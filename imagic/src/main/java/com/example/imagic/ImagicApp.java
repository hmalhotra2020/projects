package com.example.imagic;

import com.example.imagic.config.Config;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class ImagicApp implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(ImagicApp.class);

	@Autowired
	MeterRegistry meterRegistry;

	@Autowired
	Config config;

	public static void main(String[] args) {
		SpringApplication.run(ImagicApp.class, args);
	}

	@Bean
	MeterRegistryCustomizer<SimpleMeterRegistry> metricsCommonTags() {
		return registry -> registry.config().commonTags("app.name", "com.example");
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
