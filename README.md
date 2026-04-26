# 🏪 Stock Inventory Java

A Java-based inventory management system for retail stores that tracks products across the full lifecycle: **Deposit → Store → Sold**. It supports perishable and electronic products, role-based employee permissions, distributor management, and transaction history.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Class Architecture](#class-architecture)
- [Data Files](#data-files)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Design Patterns](#design-patterns)

---

## Overview

The system models a real retail operation where:

1. Products arrive from distributors and are stored in a **Deposit** (warehouse)
2. Employees transfer stock from the Deposit to a physical **Store**
3. Cashiers process **sales** from the Store shelves
4. All transactions are logged and used to generate reports

---

## Features

- 📦 **Inventory management** — track products by location (shelf/district) in both deposit and store
- 🔄 **Three transaction types** — Supply (incoming), Internal (deposit → store), Out (sale)
- 🧑‍💼 **Role-based access control** — Administrators, Managers, and Cashiers have different permissions
- 🥛 **Perishable products** — expiration date tracking, storage temperature checks, automatic 30% discount when ≤ 3 days to expiry
- 💡 **Electronic products** — warranty, energetic class, and power tracking with custom comparators
- 🏭 **Distributor selection** — find the cheapest or best-rated distributor for any product
- 📊 **Reporting** — best-selling products, daily revenue, total profit
- 📁 **File-based data loading** — products, employees, distributors, and categories loaded from `.txt` files

---

## Project Structure

```
stock-inventory-java/
├── src/main/java/
│   ├── Main.java                        # Entry point + file loading + menu
│   ├── Product.java                     # Abstract base class for all products
│   ├── PerishableProduct.java           # Food/perishables with expiration logic
│   ├── ElectronicProduct.java           # Electronics with warranty & energy class
│   ├── Taxable.java                     # Interface for VAT calculation (9% / 19%)
│   ├── Category.java                    # Product category enum (Dairy, Electronics, etc.)
│   ├── StorageSpace.java                # Base class for Deposit and Store
│   ├── Deposit.java                     # Warehouse storage
│   ├── Store.java                       # Retail store shelves
│   ├── Transaction.java                 # Abstract base for all transactions
│   ├── SupplyTransaction.java           # Incoming stock from distributor
│   ├── InternalTransaction.java         # Deposit → Store transfer
│   ├── OutTransaction.java              # Sale from Store
│   ├── Employee.java                    # Employee with role and bonus logic
│   ├── Distributor.java                 # Supplier with catalog and rating
│   ├── ServiceManager.java              # Singleton orchestrating all operations
│   ├── SecurityException.java           # Thrown on unauthorized role access
│   ├── WeightLimitExceededException.java
│   ├── VolumeLimitExceededException.java
│   └── products.txt                     # Sample product data
```

---

## Class Architecture

```
Product (abstract)
├── PerishableProduct  implements Taxable  (TVA 9%)
└── ElectronicProduct  implements Taxable  (TVA 19%)

StorageSpace
├── Deposit
└── Store

Transaction (abstract)
├── SupplyTransaction     (MANAGER / ADMIN only)
├── InternalTransaction   (MANAGER / ADMIN only)
└── OutTransaction        (CASHIER only)

ServiceManager  [Singleton]
```

**Inventory data structure inside `StorageSpace`:**

```
Map<Product, Map<Integer, Integer>>
       │              │       └── quantity
       │              └── location (shelf / district ID)
       └── product
```

---

## Data Files

The application reads four plain-text files at startup from `src/main/java/`:

### `products.txt`
```
PERISHABLE,<name>,<description>,<purchasePrice>,<salePrice>,<category>,<expirationDate>,<storageTemp>
ELECTRONIC,<name>,<description>,<purchasePrice>,<salePrice>,<category>,<warrantyMonths>,<energeticClass>,<power>
```

### `distributors.txt`
```
<rating>|<paymentTermDays>|<productName>:<price>,<productName>:<price>,...
```

### `employees.txt`
```
<name>,<ROLE>,<salary>
```
Roles: `ADMINISTRATOR`, `MANAGER`, `CASHIER`

### `categories.txt`
```
<name>,<description>
```

---

## Getting Started

### Prerequisites

- Java 17 or higher
- An IDE like IntelliJ IDEA (project includes `.idea` config)

### Setup

```bash
git clone https://github.com/ioanavlad3/stock-inventory-java.git
cd stock-inventory-java
```

Create the required data files in `src/main/java/` (see [Data Files](#data-files) above), then run:

```bash
javac src/main/java/*.java
java src.main.java.Main
```

Or open the project in IntelliJ and run `Main.java` directly.

---

## Usage

The application runs an interactive console menu:

```
========== MAIN MENU ==========
1.  Display all products in Warehouse
2.  Display all categories
3.  Restock product (Supply Transaction)
4.  Transfer product to Store (Internal Transaction)
5.  Sell product (Out Transaction)
6.  Display best-selling products
7.  Display total profit
8.  Calculate total sales for a specific day
9.  Apply expiration discounts (Perishable products)
10. Find best/cheapest distributor for a product
11. Check global stock for a product
12. Filter products by category
0.  Exit
```

**Typical flow:**

```
Option 3 → Restock (Manager restocks Whole Milk from cheapest distributor)
Option 4 → Transfer (Manager moves 20 units to Store shelf 5)
Option 5 → Sell (Cashier sells 3 units at the register)
Option 7 → View total profit across all sales
```

> ⚠️ Role enforcement is strict: supplying and transferring require `MANAGER` or `ADMINISTRATOR`; selling requires `CASHIER`.

---

## Design Patterns

| Pattern | Where |
|---|---|
| **Singleton** | `ServiceManager` — single point of control for all operations |
| **Template Method** | `Transaction` — defines the skeleton (`checkPermission` → `execute` → `logOperation`), subclasses fill in the steps |
| **Strategy** | `Taxable` interface — each product type applies its own VAT rate |
| **Inheritance + Polymorphism** | `Product`, `StorageSpace`, `Transaction` hierarchies |
