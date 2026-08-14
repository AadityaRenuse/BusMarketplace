
```mermaid
erDiagram

    USERS ||--o| AGENCIES : "has profile"
    USERS ||--o{ TRIP_REQUESTS : "creates"
    USERS ||--o{ BOOKINGS : "makes"

    AGENCIES ||--o{ BUSES : "lists"
    AGENCIES ||--o{ BIDS : "submits"
    AGENCIES ||--o{ BOOKINGS : "receives"

    TRIP_REQUESTS ||--o{ BIDS : "receives"
    TRIP_REQUESTS ||--o{ BOOKINGS : "creates"

    BUSES ||--o{ BIDS : "proposed in"
    BUSES ||--o{ BOOKINGS : "reserved in"

    BIDS ||--o| BOOKINGS : "accepted as"

    BOOKINGS ||--o| REVIEWS : "receives"

    USERS {
        bigint user_id PK
        varchar name
        varchar email UK
        varchar password_hash
        varchar phone
        varchar role
        timestamp created_at
    }

    AGENCIES {
        bigint agency_id PK
        bigint user_id FK
        varchar agency_name
        varchar phone
        text address
        varchar verification_status
        timestamp created_at
    }

    BUSES {
        bigint bus_id PK
        bigint agency_id FK
        varchar bus_name
        varchar registration_number UK
        integer capacity
        varchar bus_type
        boolean is_ac
        numeric price_per_trip
        text description
        timestamp created_at
    }

    TRIP_REQUESTS {
        bigint trip_request_id PK
        bigint customer_id FK
        varchar pickup_location
        varchar destination
        date start_date
        date end_date
        integer passengers
        text requirements
        varchar status
        timestamp created_at
    }

    BIDS {
        bigint bid_id PK
        bigint trip_request_id FK
        bigint agency_id FK
        bigint bus_id FK
        numeric amount
        text message
        varchar status
        timestamp created_at
    }

    BOOKINGS {
        bigint booking_id PK
        bigint customer_id FK
        bigint agency_id FK
        bigint bus_id FK
        bigint trip_request_id FK
        bigint bid_id FK
        numeric amount
        varchar booking_type
        varchar status
        date start_date
        date end_date
        timestamp created_at
    }

    REVIEWS {
        bigint review_id PK
        bigint booking_id FK
        bigint customer_id FK
        bigint agency_id FK
        integer rating
        text comment
        timestamp created_at
    }
    