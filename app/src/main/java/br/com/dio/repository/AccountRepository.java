package br.com.dio.repository;

import java.time.OffsetDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.MoneyAudit;
import br.com.dio.model.Wallet;
import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {

    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(final List<String> pix, final long initialFunds) {
        if (!accounts.isEmpty()) {
            List<String> pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (String p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("o pix " + p + " já está em uso!");
                }
            }
        }
        AccountWallet newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount) {
        AccountWallet target = findByPix(pix);
        target.addMoney(fundsAmount, "deposito");
    }

    public long withdraw(final String pix, final long amount) {
        AccountWallet source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        AccountWallet source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        AccountWallet target = findByPix(targetPix);
        String message = "pix enviado de " + sourcePix + " para " + targetPix;
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByPix(final String pix) {
        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Chave pix: " + pix + " inexistente!"));
    }

    public List<AccountWallet> list() {
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix) {
        Wallet wallet = findByPix(pix);
        List<MoneyAudit> audit = wallet.getFinancialTransactions();
        return audit.stream()
                .collect(Collectors.groupingBy(
                        t -> t.createdAt().truncatedTo(SECONDS),
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

}
