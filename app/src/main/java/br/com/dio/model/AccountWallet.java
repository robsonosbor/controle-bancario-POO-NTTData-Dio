package br.com.dio.model;

import lombok.Getter;
import java.util.List;

import static br.com.dio.model.BankService.ACCOUNT;

@Getter
public class AccountWallet extends Wallet {

    private final List<String> pix;

    public AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount, "valor de criação da conta");
    }

    public void addMoney(final long amount, final String description) {
        List<Money> money = generateMoney(amount, description);
        this.money.addAll(money);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "service=" + super.getService() +
                ", money= R$ " + super.money.size() / 100 + "," + super.money.size() % 100 +
                "}" + " AccountWallet{" + "pix=" + pix + '}';
    }
}