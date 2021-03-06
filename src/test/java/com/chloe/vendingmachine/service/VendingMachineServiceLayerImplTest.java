/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chloe.vendingmachine.service;

import com.chloe.vendingmachine.dao.VendingMachineAuditDao;
import com.chloe.vendingmachine.dao.VendingMachineAuditDaoFileImpl;
import com.chloe.vendingmachine.dao.VendingMachineDao;
import com.chloe.vendingmachine.dao.VendingMachineDaoFileImpl;
import com.chloe.vendingmachine.dao.VendingMachinePersistenceException;
import com.chloe.vendingmachine.dto.Item;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author User
 */
public class VendingMachineServiceLayerImplTest {
    
    VendingMachineDao testDao = new VendingMachineDaoFileImpl("VendingMachineTestFile.txt");  
    String testAuditFile = "testAuditFile.txt";
    VendingMachineAuditDao testAuditDao = new VendingMachineAuditDaoFileImpl(testAuditFile);
    VendingMachineServiceLayer testService = new VendingMachineServiceLayerImpl(testAuditDao,testDao);
    
    
    public VendingMachineServiceLayerImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        
    }
    @Test
    public void testCheckIfEnoughMoney() {
        //ARRANGE
        Item hariboClone = new Item("Haribo");
        hariboClone.setCost(new BigDecimal("1.60"));
        hariboClone.setInventory(9);
        
        BigDecimal enoughMoney = new BigDecimal("2.00");
        BigDecimal notEnoughMoney = new BigDecimal("1.59");
        
        //ACT - enough money
        try {
            testService.checkIfEnoughMoney(hariboClone, enoughMoney);
        } catch (InsufficientFundsException e){
            fail("There is sufficient funds, exception should not have been thrown");
        }
        //ACT - not enough money
        try {
            testService.checkIfEnoughMoney(hariboClone, notEnoughMoney);
            fail("There insufficient funds, exception should have been thrown");
        } catch (InsufficientFundsException e){
        }
    }
    
    @Test
    public void testGetItemsInStockWithCosts() {
        
    }
    
    @Test
    public void testGetChangePerCoin(){
        //ARRANGE
        Item hariboClone = new Item("Haribo");
        hariboClone.setCost(new BigDecimal("1.60"));
        hariboClone.setInventory(9);
        
        BigDecimal money = new BigDecimal("2.50");
        
        //Change should be $0.90: 25c: 3, 10c: 1, 5c:1
        Map<BigDecimal, BigDecimal> expectedChangePerCoin = new HashMap<>();
        expectedChangePerCoin.put(new BigDecimal("0.25"), new BigDecimal("3"));
        expectedChangePerCoin.put(new BigDecimal("0.10"), new BigDecimal("1"));
        expectedChangePerCoin.put(new BigDecimal("0.05"), new BigDecimal("1"));
        
        //ACT
        Map<BigDecimal, BigDecimal> changePerCoin = testService.getChangePerCoin(hariboClone, money);
        
        //ASSERT
        assertEquals(changePerCoin.size(), 3, "There should be three coins");
        
        //assertTrue(changePerCoin.entrySet().equals(expectedChangePerCoin.entrySet()), "The change per coin map should be equal to the expected.");
        //need to find out how to compare these as this doesnt work
    }
    
    @Test
    public void testGetItem() throws InsufficientFundsException, VendingMachinePersistenceException, NoItemInventoryException {
        //ARRANGE
        Item snickersClone = new Item("Snickers");
        snickersClone.setCost(new BigDecimal("2.10"));
        snickersClone.setInventory(0);
        BigDecimal money = new BigDecimal("3.00");
        
        Item malteasersClone = new Item("Malteasers");
        malteasersClone.setCost(new BigDecimal("2.10"));
        malteasersClone.setInventory(testDao.getItemInventory("Malteasers"));
        
        Item itemWanted = null;
        //ACT
        try {
            itemWanted = testService.getItem("Snickers", money);
            fail("The item wanted is out of stock.");
        }catch (NoItemInventoryException e) {
        }
        try {
            itemWanted = testService.getItem("Malteasers", money);
        }catch (NoItemInventoryException e) {
            if (testDao.getItemInventory("Malteasers")>0){
            fail("The item wanted is in stock.");
        } 

        //ASSERT
        assertNotNull(itemWanted, "change should not be null");
        assertEquals(itemWanted, malteasersClone,"The item retrieved should be snickers");
    }
    }
    
    @Test
    public void testRemoveOneItemFromInventory() throws VendingMachinePersistenceException {
        //ARRANGE
        String name = "Snickers";
        
        //There are no snickers left
        try{
            //ACT
            testService.removeOneItemFromInventory(name);
            //ASSERT
            fail("There are no snickers left, exception should be thrown");
        } catch (NoItemInventoryException e) {  
        }
        
        String malteasers = "Malteasers";
        try{
            //ACT
            testService.removeOneItemFromInventory(malteasers);
        } catch (NoItemInventoryException e) {
            if (testDao.getItemInventory(malteasers)>0){
                fail("Malteasers are in stock, exception should not be thrown");
            }
        } 
    }
    
    
}
