# Spring Boot Stripe Payment Application

A complete Spring Boot application with Thymeleaf frontend that integrates Stripe payments and stores payment data in MySQL.

## Features

- **Thymeleaf-based checkout page** at `/checkout` with Stripe Elements integration
- **Stripe PaymentIntent API** integration for secure payments
- **Webhook handling** for payment confirmations
- **MySQL database** storage for payment records
- **30-day subscription** tracking with automatic date calculation

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Stripe account (for API keys)

## Setup Instructions

### 1. Database Setup

Create a MySQL database:
```sql
CREATE DATABASE stripe_payments;
```

The application will automatically create the necessary tables using JPA/Hibernate.

### 2. Environment Variables

Set the following environment variables:

```bash
# Database Configuration
export DB_USERNAME=your_mysql_username
export DB_PASSWORD=your_mysql_password

# Stripe Configuration
export STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
export STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_publishable_key
export STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

### 3. Install Dependencies

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Frontend
- `GET /checkout` - Checkout page with payment form

### Backend
- `POST /create-payment-intent` - Creates Stripe PaymentIntent
- `POST /webhook` - Stripe webhook endpoint for payment confirmations

## Database Schema

The `Payment` entity includes:
- `id` - Primary key
- `stripe_payment_id` - Stripe payment identifier
- `status` - Payment status
- `amount` - Payment amount (in dollars)
- `currency` - Payment currency
- `email` - Customer email
- `start_date` - Subscription start date (today)
- `end_date` - Subscription end date (+30 days)
- `created_at` - Record creation timestamp

## Stripe Webhook Configuration

1. In your Stripe Dashboard, go to **Webhooks**
2. Add a new webhook endpoint: `https://yourdomain.com/webhook`
3. Select the event: `payment_intent.succeeded`
4. Copy the webhook signing secret to your environment variables

## Testing

### Test Card Numbers (Stripe)
- **Success**: 4242 4242 4242 4242
- **Decline**: 4000 0000 0000 0002
- **3D Secure**: 4000 0025 0000 3155

Use any future expiry date, any 3-digit CVC, and any postal code.

## Configuration

The application uses `application.yml` for configuration:

- Database connection settings
- Stripe API configuration
- Thymeleaf template settings
- Logging configuration

## Security Features

- Stripe webhook signature verification
- Environment variable-based configuration
- Input validation and error handling
- Secure payment processing with Stripe Elements

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/example/stripepaymentapp/
│   │   ├── controller/
│   │   │   ├── CheckoutController.java
│   │   │   └── WebhookController.java
│   │   ├── entity/
│   │   │   └── Payment.java
│   │   ├── repository/
│   │   │   └── PaymentRepository.java
│   │   └── StripePaymentAppApplication.java
│   └── resources/
│       ├── application.yml
│       └── templates/
│           └── checkout.html
└── test/
```

### Key Dependencies
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Boot Thymeleaf
- MySQL Connector/J
- Stripe Java SDK 24.16.0

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL is running
   - Verify database credentials
   - Ensure database exists

2. **Stripe API Errors**
   - Verify API keys are correct
   - Check Stripe dashboard for test mode
   - Ensure webhook secret is set

3. **Webhook Not Working**
   - Verify webhook URL is accessible
   - Check webhook secret configuration
   - Review Stripe webhook logs

### Logs

The application logs payment processing and webhook events. Check the console output for debugging information.