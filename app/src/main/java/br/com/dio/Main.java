package br.com.dio;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.NoFundsEnoughException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.Investment;
import br.com.dio.model.InvestmentWallet;
import br.com.dio.repository.AccountRepository;
import br.com.dio.repository.InvestmentRepository;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AccountRepository accountRepository = new AccountRepository();
    private static final InvestmentRepository investmentRepository = new InvestmentRepository();

    public static void main(String[] args) {
        System.out.println("Ola seja bem vindo ao DIO Bank");
        while (true) {
            System.out.println("Selecione a operacao desejada");
            System.out.println(" 1 - Criar uma conta");
            System.out.println(" 2 - Criar um investimento");
            System.out.println(" 3 - Criar uma carteira de investimento");
            System.out.println(" 4 - Depositar na conta");
            System.out.println(" 5 - Sacar da conta");
            System.out.println(" 6 - transferencia entre contas");
            System.out.println(" 7 - Investir");
            System.out.println(" 8 - Sacar investimento");
            System.out.println(" 9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimentos");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Historico de conta");
            System.out.println("14 - Sair");
            int option = scanner.nextInt();
            switch (option) {
                case 1 ->
                    createAccount();
                case 2 ->
                    createInvestment();
                case 3 ->
                    createWalletInvestment();
                case 4 ->
                    deposit();
                case 5 ->
                    withdraw();
                case 6 ->
                    transferToAccount();
                case 7 ->
                    incInvestment();
                case 8 ->
                    rescueInvestment();
                case 9 ->
                    accountRepository.list().forEach(System.out::println);
                case 10 ->
                    investmentRepository.list().forEach(System.out::println);
                case 11 ->
                    investmentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investmentRepository.updateAmount();
                    System.out.println("investimentos reajustados");
                }
                case 13 ->
                    checkHistory();
                case 14 ->
                    System.exit(0);
                default ->
                    System.out.println("Opção inválida. Por favor, escolha uma opção válida.");
            }
        }
    }

    private static void createAccount() {
        System.out.println("informe as chaves pix, separanado-as por ';' ");
        List<String> pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("informe o valor inicial do depósito: ");
        long amount = scanner.nextLong();
        AccountWallet wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada: " + wallet);
    }

    private static void createInvestment() {
        System.out.println("informe a taxa do investimento: ");
        int tax = scanner.nextInt();
        System.out.println("informe o valor inicial do depósito: ");
        long initialFunds = scanner.nextLong();
        Investment investment = investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado: " + investment);
    }

    private static void withdraw() {
        System.out.println("informe a chave pix da conta para saque: ");
        String pix = scanner.next();
        System.out.println("informe o valor do saque: ");
        long amount = scanner.nextLong();
        try {
            accountRepository.withdraw(pix, amount);
        } catch (AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("informe a chave pix da conta para deposito: ");
        String pix = scanner.next();
        System.out.println("informe o valor do depósito: ");
        long amount = scanner.nextLong();
        try {
            accountRepository.deposit(pix, amount);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private static void transferToAccount() {
        System.out.println("informe a chave pix da conta de origem: ");
        String source = scanner.next();
        System.out.println("informe a chave pix da conta de destino: ");
        String target = scanner.next();
        System.out.println("informe o valor do deposito: ");
        long amount = scanner.nextLong();
        try {
            accountRepository.transferMoney(source, target, amount);
        } catch (AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void createWalletInvestment() {
        System.out.println("informe a chave pix da conta: ");
        String pix = scanner.next();
        AccountWallet account = accountRepository.findByPix(pix);
        System.out.println("informe o identificador do investimento: ");
        int investmentId = scanner.nextInt();
        InvestmentWallet investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("Conta de investimento criada: " + investmentWallet);
    }

    private static void incInvestment() {
        System.out.println("informe a chave pix da conta para investimento: ");
        String pix = scanner.next();
        System.out.println("informe o valor do depósito: ");
        long amount = scanner.nextLong();
        try {
            investmentRepository.deposit(pix, amount);
        } catch (WalletNotFoundException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void rescueInvestment() {
        System.out.println("informe a chave pix da conta para resgate do investimento: ");
        String pix = scanner.next();
        System.out.println("informe o valor do saque: ");
        long amount = scanner.nextLong();
        try {
            investmentRepository.withdraw(pix, amount);
        } catch (AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void checkHistory() {
        System.out.println("informe a chave pix da conta para verificar historico: ");
        String pix = scanner.next();
        AccountWallet wallet;
        try {
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k, v) -> {
                System.out.println(k.format(ISO_DATE_TIME));
                if (!v.isEmpty()) {
                    System.out.println(v.get(0).transactionId());
                    System.out.println(v.get(0).description());
                }
                System.out.println("R$ " + (v.size() / 100) + "," + (v.size() % 100));
            });

        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
