package com.weshaka.ole.repository;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.JedisPoolConfig;

import com.github.fakemongo.Fongo;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;
import com.weshaka.ole.OleSvcApplication;
import com.weshaka.ole.entity.BeaconSubject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { OleSvcApplication.class }, loader = SpringApplicationContextLoader.class)
@ActiveProfiles({ "test" })
public class BeaconSubjectRepositoryTests {
    @Profile("test")
    @Configuration
    static class LocalRedisConfig {
        @Bean
        @DependsOn("redisTemplate")
        public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
            final RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);

            // Number of seconds before expiration. Defaults to unlimited (0)
            cacheManager.setDefaultExpiration(10);// In seconds
            return cacheManager;
        }

        @Bean(name = "redisConnectionFactory")
        public RedisConnectionFactory jedisConnectionFactory() {
            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            final JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
            return jedisConnectionFactory;
        }

        @SuppressWarnings("rawtypes")
        @Bean(name = "redisTemplate")
        @DependsOn("redisConnectionFactory")
        public RedisTemplate redisTemplate() {
            final StringRedisTemplate redisTemplate = new StringRedisTemplate(jedisConnectionFactory());
            return redisTemplate;
        }
    }

    @Profile("test")
    @Configuration
    @EnableMongoRepositories
    static class MongoDbTestConfig extends AbstractMongoConfiguration {
        @Override
        protected String getDatabaseName() {
            return "ole-svc-test";
        }

        @Bean
        @Override
        public Mongo mongo() {
            return new Fongo("InMemoryMongo").getMongo();
        }
    }

    @ClassRule
    public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule().build();

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("ole-svc-test");

    @Autowired
    private ApplicationContext appliactionContext;

    @Autowired
    private BeaconSubjectRepositoryCustom beaconSubjectRepositoryCustom;

    @Autowired
    private BeaconSubjectRepository beaconSubjectRepository;

    @Test
    @UsingDataSet(locations = { "/BeaconSubjectWithDemoBeacon.json" }, loadStrategy = LoadStrategyEnum.INSERT)
    public void testFindBeaconSubjectByBeaconMac() {
        final Optional<BeaconSubject> beaconSubject = beaconSubjectRepositoryCustom.findBeaconSubjectByBeaconMac("C1:5C:A0:2A:EC:F0");
        assertNotNull(beaconSubject.get().getBeacon().toString());
        assertNotNull(beaconSubject);
    }

    @Test
    @UsingDataSet(locations = { "/BeaconSubjectWithDemoBeacon.json" }, loadStrategy = LoadStrategyEnum.INSERT)
    public void testFindBeaconSubjectById() {
        final Optional<BeaconSubject> beaconSubject = beaconSubjectRepositoryCustom.findBeaconSubjectByBeaconMac("C1:5C:A0:2A:EC:F0");
        final BeaconSubject sameBeaconSubject = beaconSubjectRepository.findOne(beaconSubject.get().getId());
        final BeaconSubject sameoldBeaconSubject = beaconSubjectRepository.findById(beaconSubject.get().getId());
        assertNotNull(sameoldBeaconSubject);
        assertNotNull(sameBeaconSubject);
        assertEquals(sameBeaconSubject.getId(), beaconSubject.get().getId());
        assertEquals(sameoldBeaconSubject.getId(), beaconSubject.get().getId());
    }
}
