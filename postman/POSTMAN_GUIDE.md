 # Postman Collection Guide

## Import Instructions

### Import Collection
1. Open Postman
2. Click "Import" button
3. Select file: `MELI_Order_Management_System.postman_collection.json`
4. Click "Import"

### Import Environment
1. Click on "Environments" (left sidebar)
2. Click "Import"
3. Select file: `Local_Development.postman_environment.json`
4. Click "Import"
5. Select "Local Development" from environment dropdown (top right)

## Running the Collection

### Prerequisites
- Application must be running on http://localhost:8080
- Database must be accessible

### Test Individual Requests
1. Expand the collection folders
2. Click on any request
3. Click "Send" button
4. View response in the response panel

### Run Complete Test Suite
1. Click on collection name
2. Click "Run" button
3. Select requests to run
4. Click "Run MELI Order Management System"
5. View test results

## Request Order

Follow this order for initial setup:

1. Client Management
   - Create Client (saves client_id to environment)
   
2. Item Management
   - Create Item (saves item_id to environment)
   
3. Order Management
   - Create Order (uses client_id and item_id from environment)
   - Test other order operations

## Environment Variables

The collection uses these variables:
- base_url: API base URL
- client_id: Created client ID (auto-updated)
- item_id: Created item ID (auto-updated)
- order_id: Created order ID (auto-updated)

## Testing Tips

1. Run "Create" requests first to populate the database
2. Variable IDs update automatically after successful creation
3. Check the "Tests" tab for automated validation
4. View console (bottom) for detailed test results
5. Use "Pre-request Script" tab for setup if needed

## Troubleshooting

**Problem: 404 errors on all requests**
- Ensure application is running
- Check base_url environment variable

**Problem: 400 validation errors**
- Check request body matches required format
- Verify all required fields are present

**Problem: Variables not updating**
- Check "Tests" tab has save script
- Verify environment is selected (top right)

## Collection Structure

```
MELI Order Management System/
├── Client Management/ (10 requests)
│   ├── CRUD operations
│   └── Query operations
├── Item Management/ (14 requests)
│   ├── CRUD operations
│   └── Query operations
├── Order Management/ (17 requests)
│   ├── CRUD operations
│   ├── Status management
│   └── Query operations
└── Test Scenarios/
    ├── Complete workflows
    └── Error handling tests
```

## Contact

For issues or questions about this collection:
- Check API documentation in collection description
- Verify application logs for errors
- Ensure database is properly configured