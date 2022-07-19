pub mod schema;
pub mod models;
pub mod db_connection;
pub mod handlers; // This goes to the top to load the next handlers module 

#[macro_use]
extern crate diesel;
extern crate dotenv;
extern crate serde;
extern crate serde_json;
#[macro_use] 
extern crate serde_derive;

extern crate actix;
extern crate actix_web;
extern crate futures;
use actix_web::{HttpServer, App, web, HttpRequest, HttpResponse};

fn main() {
    // We are creating an Application instance and 
    // register the request handler with a route and a resource 
    // that creates a specific path, then the application instance 
    // can be used with HttpServer to listen for incoming connections.
    let sys = actix::System::new("mystore");

    HttpServer::new(
    || App::new()
        .service(
            web::resource("/products")
                .route(web::get().to_async(handlers::products::index))
        ))
    .bind("127.0.0.1:8088").unwrap()
    .start();

    println!("Started http server: 127.0.0.1:8080");
    let _ = sys.run();
}