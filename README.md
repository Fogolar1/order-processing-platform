
# Order Processing Platform

An event driven order processing platform using Apache Kafka built in Spring 3. The full platform is divided by 3 modules (microservices): inventory-service, order-service and payment-service. All 3 communicate using only kafka events and serve as mockup for a real production environment order processing platform.


## Run Locally

Clone the project

```bash
  git clone https://github.com/Fogolar1/order-processing-platform.git
```

Go to the project directory

```bash
  cd order-processing-platform
```

Build docker image

```bash
  docker compose up --build -d
```

If you want to check logs

```bash
  docker compose logs -f order-service inventory-service payment-service
```


## How to run

### Important! Every service runs in a diferent localhost port:
#### Inventory: 8082
#### Orders: 8081
#### Payments: 8080

#### Create orders

```http
  POST /orders
```
Body:
```json
  {
    "productId" : String,
    "quantity" : int,
    "amount" : BigDecimal
  }
```

#### Get order

```http
  GET /orders/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of order to fetch |

#### Get all orders

```http
  GET /orders
```

#### Update/Add product stock

```http
  PUT /inventory/${productId}
```
Body:
```json
  {
    "available" : int
  }
```

#### Get product

```http
  GET /inventory/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of inventory to fetch |


#### Get all products

```http
  GET /inventory
```
