**Problem Statement**

Develop a program called ATM. It allows customers to deposit and withdraw in these denominations: 20, 10, 5, and 1 dollar bills.

Deposit: Customer inputs the number of currency notes in each denomination

- If any input values are negative, print "Incorrect deposit amount".
- If all the input values are zero, print "Deposit amount cannot be zero".
- If the input values are valid, increment the balances of corresponding dollar bills and print the available new balances in each denomination and the total balance.

Withdraw: Customer input the amount to withdraw. ATM dispenses the 20, 10, 5, and 1 dollar bills as needed. 

- If the input amount is zero, negative, or over the current balance, print "Incorrect or insufficient funds".
- If the input amount is in valid range, print the number of current notes dispensed in each denomination. Use the available higher denomination first. Also, print the available new balances in each denomination and the total balance.

For example, 

Deposit 1: 10s: 8, 5s: 20 <br/>
Balance: 20s=0, 10s=8, 5s=20, 1s=0, Total=180

Deposit 2: 20s: 3, 5s: 18, 1s: 4 <br/>
Balance: 20s=3, 10s=8, 5s=38, 1s=4, Total=334

Withdraw 1: 75 <br/>
Dispensed: 20s=3, 10s=1, 5s=1 <br/>
Balance: 20s=0, 10s=7, 5s=37, 1s=4, Total=259

Withdraw 2: 122 <br/>
Dispensed: 10s=7, 5s=10, 1s=2 <br/>
Balance: 20s=0, 10s=0, 5s=27, 1s=2, Total=137 <br/>

Withdraw 3: 253 <br/>
Output: "Incorrect or insufficient funds" <br/>

Withdraw 4: 0 <br/>
Output: "Incorrect or insufficient funds" <br/>

Withdraw 5: -25 <br/>
Output: "Incorrect or insufficient funds" <br/>

Tips: This program should be expandable to support 50s and 100s in future. Please allow the program to support any denominations with little or no code change.

public class ATM {
   public void deposit(...) {
   }

   public void withdraw(...) {
   }
}
**********


**Important Classes**

**ATM:**
This class defined the business logic for processing deposit and withdraw requests

**Parser:** 
Helper class to parse user input. Input is accepted as similar to the example given

**TransactionRequest:**
User Input Holder

**App:**
Starting point of the command line application

**ATMTestCases:**
The Test class for the ATM
    - testAllExampleCases: Test the example given in problem statement as it is matching the final output
    - testInSteps: Test the ATM business logic step by step including negative test cases

**ParserTestCases:** 
Positive test cases so far added for argument parsing.

