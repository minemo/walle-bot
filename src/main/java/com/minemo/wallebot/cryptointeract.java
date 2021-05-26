package com.minemo.wallebot;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

//Class for simpler interaction with the blockchain/cryptocurrency

public class cryptointeract {

    private final Web3j web3;
    private final Random rand = new Random();

    public cryptointeract(String url) {
        this.web3 = Web3j.build(new HttpService(url));
    }

    public String getlastblock() throws IOException {
        EthBlockNumber blockNumber = web3.ethBlockNumber().send();
        return blockNumber.getBlockNumber().toString();
    }

    public String getVersion() throws IOException {
        Web3ClientVersion clientversion = web3.web3ClientVersion().send();
        return clientversion.getWeb3ClientVersion();
    }

    public List<String> getAccount() throws IOException {
        EthAccounts accounts = web3.ethAccounts().send();
        return accounts.getAccounts();
    }

    public BigInteger getbalance(String address) throws IOException {
        EthBlockNumber latestblock = web3.ethBlockNumber().send();
        EthGetBalance balance = web3.ethGetBalance(address, DefaultBlockParameter.valueOf(latestblock.getBlockNumber())).send();
        return balance.getBalance();
    }

    public void transact(String from, String to, float amnt, String data) throws IOException {
        //TODO add function for sending funds/data between accounts
        EthBlockNumber latestblock = web3.ethBlockNumber().send();
        Transaction transact = new Transaction(from, web3.ethGetTransactionCount(from, DefaultBlockParameter.valueOf(latestblock.getBlockNumber())).send().getTransactionCount(), BigInteger.valueOf(2000000000L), BigInteger.valueOf((long) 6721975f), to, BigInteger.valueOf((long) amnt), data);
        web3.ethSendTransaction(transact).send();
    }

    public void shuffle() throws IOException {
        //TODO add function for randomly shuffling funds between accounts
    }
}
