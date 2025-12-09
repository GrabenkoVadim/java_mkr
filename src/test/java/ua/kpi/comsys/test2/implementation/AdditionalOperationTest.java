/*
 * Copyright (c) 2014-2015, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdditionalOperationTest {


    NumberListImpl list1;
    NumberListImpl list2;
    NumberListImpl list3;
    NumberListImpl result;

    @After
    public void tearDown() {
        if (list1 != null) {
            list1.clear();
            list1 = null;
        }
        if (list2 != null) {
            list2.clear();
            list2 = null;
        }
        if (list3 != null) {
            list3.clear();
            list3 = null;
        }
        if (result != null) {
            result.clear();
            result = null;
        }
    }

    @Test
    public void testAdd() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 0);

        list1 = new NumberListImpl("2");
        list2 = new NumberListImpl("15");
        list3 = new NumberListImpl("17");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }


    @Test
    public void testRemove() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 1);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("13");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }


    @Test
    public void testMultiply() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 2);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("30");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }


    @Test
    public void testDiv() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 3);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("7");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }

    @Test
    public void testMod() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 4);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("1");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }

    @Test
    public void testAND() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 5);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("2");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }

    @Test
    public void testOR() {
        assumeTrue(NumberListImpl.getRecordBookNumber() % 7  == 6);

        list1 = new NumberListImpl("15");
        list2 = new NumberListImpl("2");
        list3 = new NumberListImpl("15");

        result = list1.additionalOperation(list2);

        assertNotNull("Result is null while it shouldn't!", result);
        assertEquals("Additional operation implemented in a wrong way",list3,result);
    }
}
