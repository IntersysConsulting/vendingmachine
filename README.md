## Design the controls for a vending machine
# Requirements:
- 10 slots total (columns A - E, rows 1 - 2)
- 15 items maximum per slot
- Items in a given slot must have matching SKUs
- A SKU is an integer identifying a product (e.g., Butterfingers could have a SKU of 1002, Snickers could have a SKU of 5002, etc.)
## Items contain the following information
- name : String (e.g., Snickers, Twizzlers, Twix, Butterfingers, Milky Way, etc.)
- sku : Integer (e.g., 1000, 1005, 40001, 123123, etc.) NOTE: all SKUs are greater than 0
- price : Double (e.g., 1.25 for $1.25)

# Valid Inputs:
- Users can make selections by providing a String indicating the slot ("A7")
- Users can enter money into the vending machine
- The vending machine supports $0.05, $0.10, $0.25, $1, and $5, all other currency should be rejected
- Users can cancel their order 
# Supported Outputs (printed to the console is fine):
- Error messages (item out of stock, order canceled, unsupported money (e.g., $0.01, $10.00, etc.))
- Dispensed order
- Dispensed change (when valid)
- A display of the price of the item
# Expected Behavior: 
- A user can cancel an order until it is dispensed
- A message should be output to the user when an item is dispensed
- A message should be output to the user when change is dispensed
- A message should be output to the user if there is not enough input to dispense the order (not enough money, not enough product)

Example Scala classes:

class Item(val name : String, val sku : Int, val price : Double)
class Selection(val input : String)
class InsertMoney(val amount : Double)
class CancelOrder()

Example outputs printed to the screen:

1. Item out of stock
2. Order canceled
3. Invalid selection 
4. Dispensed item A7
5. Dispensed change: $0.25
6. Item A7 costs $1.25

