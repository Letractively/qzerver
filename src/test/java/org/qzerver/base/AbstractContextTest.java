package org.qzerver.base;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        // Собственный агрузчик контекстов
        loader = WebAppContextLoader.class,
        // Реальная загрузка контекстов осуществляется загрузчиком WebAppContextLoader достаточно независимо
        // от перечисленных здесь контекстов. Основная цель перечисления контекстов здесь - помощь для IDE в
        // удобной навигации по бинам.
        locations = {
                "classpath:/configuration/context/model/root.xml",
                "classpath:/configuration/context/webapp/root.xml",
                "classpath:/configuration/context/servlet/root.xml",
                "classpath:/test/context/test-model.xml",
                "classpath:/test/context/test-servlet.xml"
        }
)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractContextTest {
}
