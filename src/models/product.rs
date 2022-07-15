use crate::schema::products;

// This will tell the compiler that the struct will be serialized and 
// deserialized, we need to install serde to make it work.
#[derive(Serialize, Deserialize)] 
pub struct ProductList(pub Vec<Product>);

#[derive(Queryable, Serialize, Deserialize)]
pub struct Product {
    pub id: i32,
    pub name: String,
    pub stock: f64,
    pub price: Option<i32> // For a value that can be null, 
                           // in Rust is an Option type that 
                           // will be None when the db value is null
}

#[derive(Insertable)]
#[table_name="products"]
pub struct NewProduct {
    pub name: Option<String>,
    pub stock: Option<f64>,
    pub price: Option<i32>
}

impl ProductList {
    pub fn list() -> Self {
        // These four statements can be placed in the top, or here, your call.
        use diesel::RunQueryDsl;
        use diesel::QueryDsl;
        use crate::schema::products::dsl::*;
        use crate::db_connection::establish_connection;

        let connection = establish_connection();

        let result = 
            products
                .limit(10)
                .load::<Product>(&connection)
                .expect("Error loading products");

        // We return a value by leaving it without a comma
        ProductList(result)
    }
}