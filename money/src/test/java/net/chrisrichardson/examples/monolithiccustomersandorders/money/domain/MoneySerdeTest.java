package net.chrisrichardson.examples.monolithiccustomersandorders.money.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class MoneySerdeTest {

    private final Money money = new Money("1.23");
    private final String jsonForMoney = "\"1.23\"";
    private final MoneyContainer moneyContainer = new MoneyContainer(money);
    private final String jsonForMoneyContainer = "{\"amount\":\"1.23\"}";

    @Autowired
    private JacksonTester<Money> moneyTester;
    @Autowired
    private JacksonTester<MoneyContainer> moneyForContainerTester;

    public record MoneyContainer(Money amount) {}


    @Configuration
    public static class Config {
    }

    @Test
    public void testWriteJson() throws IOException {
        assertThat(moneyTester.write(money)).isEqualTo(jsonForMoney);
    }

    @Test
    public void testReadJson() throws IOException {
        assertThat(moneyTester.readObject(new StringReader(jsonForMoney))).isEqualTo(money);
    }

    @Test
    public void testWriteJsonForMoneyContainer() throws IOException {
        assertThat(moneyForContainerTester.write(moneyContainer)).isEqualTo(jsonForMoneyContainer);
    }

    @Test
    public void testReadForMoneyContainer() throws IOException {
        assertThat(moneyForContainerTester.readObject(new StringReader(jsonForMoneyContainer))).isEqualTo(moneyContainer);
    }

}
