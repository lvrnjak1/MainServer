## Feature 
User Login and Registration
### Item
App must provide possibility to register new users and assign roles. Then, with their credentials, users need to be logged in
to access the app
#### Task
- Create a new branch and switch to it
- Define all user roles
- Create routes for user registration
- Create routes for user login
- Allow only authorized access
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Questions and answers
### Item
Support for working with user questions and answers in public relations module
#### Task
- Create a new branch and switch to it
- Create routes for questions
- Create routes for answers
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Products
### Item
Modeling the data to provide support for working with products as well as some basic operations with them.
#### Task
- Create a new branch and switch to it
- Create routes for products
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Offices and Employees 
### Item
Create employee profiles, offices and implement hiring and firing employees
#### Task
- Create a new branch and switch to it
- Create routes for employee profiles
- Create routes for offices
- Implement hiring and firing employees
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Notifications
### Item
Create notifications for new employment or dismissal between user management and merchant dashboard
#### Task
- Create a new branch and switch to it
- Create routes for notification
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Inventory
### Item
Monitoring office supplies and enabling office inventory reports
#### Task
- Create a new branch and switch to it
- Create routes for inventory
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Warehouse 
### Item
Provide mechanism for successful warehouse item handling
#### Task
- Create a new branch and switch to it
- Create routes for inventory
- Create a contoller class, response class and request class if needed
- Create a service that will provide CRUD operations and queries for items in warehouse
- Commit and push on your branch
- Create a pull request

## Feature 
Password Change 
### Item
Allowing admin to change user password and set new one time password, which needs to be changed on first login
#### Task
- Create a new branch and switch to it
- Create routes for creating one time password
- Specify only admin access to them
- Create routes for creating new password 
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Employee endpoint for cash register server
### Item
Cash register app logs in by sending a request to cash reg server and they have to say what is the role of the user.  
For that cash reg server needs to be able to get all user credentials for their office
#### Task
- Create a new branch and switch to it
- Establish communication with other server
- Create needed routes
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Notifications#2
### Item
Merchant needs to notify admin when he wants to register a new office
#### Task
- Create a new branch and switch to it
- Create routes for notification
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
PR products and products on sale
### Item
Public realations app need to have access to their products. Certain products are on sale and must be updated
#### Task
- Create a new branch and switch to it
- Create routes for products in PR
- Add implementation for products on sale
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Cash Registers for Office 
### Item
Office can access their cash registers
#### Task
- Create a new branch and switch to it
- Create routes for cash registers
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Email password reset
### Item
If the password is forgotten, merchant can enter new password with given token and login successfully
#### Task
- Create a new branch and switch to it
- Create routes for sending email with token
- Validate token which user entered
- Use existing methods for encripting new password 
- Link new password to user
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Privilege changes
### Item
Admin and user manager can change employees's privideges, and with that, change their roles and assignments in business
#### Task
- Create a new branch and switch to it
- Create routes for allowing role changes
- Check user's current assignments and make adjustments to enable new ones 
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Employment logs
### Item
User manager has insight into employment history for certain employee within business
#### Task
- Create a new branch and switch to it
- Create routes for providing employment logs
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Warehouse logs
### Item
Warehouse web app has insight into history of warehouse supplies of products 
#### Task
- Create a new branch and switch to it
- Create routes for providing warehouse logs
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Inventory logs
### Item
Providing inventory logs
#### Task
- Create a new branch and switch to it
- Create routes for providing inventory logs
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
PR reviews
### Item
Site visitors can leave comments and rate displayed products
#### Task
- Create a new branch and switch to it
- Create routes for saving reviews
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature 
Transactions
### Item
Main server must realize communication with cash register server and support whole payment process
#### Task
- Create a new branch and switch to it
- Create routes for communicating with server
- Create routes for transaction processing
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
VAT
### Item
Admin should be able to add possible VATs for products and Warehouse manager should be able to set vat for each product. Database should come with 0 and 17% vat by default as that is the case in Bosnia. Vat should be classified into active and inactive.
#### Task
- Create a new branch and switch to it
- Create routes for getting all vat rates, adding new vat rate, getting all active vat rates and switching vat rate
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Tables in a restaurant
### Item
If the business is a restaurant, server should be able to keep track of the tables inside an office. Tables can be added by the admin and information about them exposed via endpoints to PR Web app and Cash register server. The table identifier should be a string
#### Task
- Create a new branch and switch to it
- Create routes for getting tables for business and office, adding new table, deleting table and getting tables for office
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#3
### Item
Warehouse needs to be notified when a merchant asks him for a certain amount of product for an office
#### Task
- Create a new branch and switch to it
- Create routes for notification
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#4
### Item
Merchant needs to be notified when the products have been delivered to the office or when products cannot be delivered to the office
#### Task
- Create a new branch and switch to it
- Create routes for notification
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#5
### Item
Merchant needs to receive notification when the request to open or close an office is rejected by the admin.
#### Task
- Create a new branch and switch to it
- Create routes for notification
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#6
### Item
When a new office is open, user management needs to receive notification 
#### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#7
## Item
Warehouse needs to be notified when a merchant asks for a certain amount of product for an office.
The warehouse can either accept or reject the request and the merchant gets feedback on it.
### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#8
### Item
The merchant needs to send a notification when the seat office changes
### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request


## Feature
Notifications#9
### Item
The merchant needs to send a notification to request products from warehouses for one of the offices.
### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request



## Feature
Notifications#10
### Item
User management web app receives notification when merchant logs in to merchant dashboard web app.
### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Notifications#11
### Item
User management web app needs to receive notification when users change their password.
### Task
- Create a new branch and switch to it
- Implement sending appropriate notifications via Log server
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
User reservation
### Item
PR Web app must be able to save user reservation and delete it via a code.
#### Task
- Create a new branch and switch to it
- Create routes for getting all reservations for office in business, making reservation, getting and changing reservation duration, canceling and verifying reservation and resending code
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Closing the cash register
### Item
An admin must be able to manage the closing of the cash register.
#### Task
- Create a new branch and switch to it
- Create routes for closing the cash register
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Configurations
### Item
An admin must be able to configure everything in the ecosystem
#### Task
- Create a new branch and switch to it
- Create route for allowing admin to manage the number of cash registers for an office
- Create route for allowing admin to manage the synchronization time of the main and cash register servers.
- Create route for allowing admin to manage the number of the offices for a business
- Create route for allowing admin to change the language for the mobile seller app
- Create route for allowing  admin to change the duration of the reservation
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request

## Feature
Managing notifications
### Item
An admin must be able to receive notifications that they are interested in
#### Task
- Create a new branch and switch to it
- Notifications when merchant requests opening for an office
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request


## Feature
Polling
### Item
Change polling to notifications
#### Task
- Create a new branch and switch to it
- Create routes for polling
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request]

## Feature
Batch delivery 
### Item
Notification for batch delivery of products to the office and batch sending products to the office
#### Task
- Create a new branch and switch to it
- Create routes for batch delivery 
- Create a contoller class, response class and request class if needed
- Commit and push on your branch
- Create a pull request


