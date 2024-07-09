package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

@ActiveProfiles({"jdbc", "postgres"})
public class JdbcUserServiceTest extends AbstractUserServiceTest {
}
