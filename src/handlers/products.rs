use actix_web::{HttpRequest, HttpResponse };

use crate::models::product::ProductList;

// This is calling the list method on ProductList and 
// serializing it to a json response
pub fn index(_req: HttpRequest) -> HttpResponse {
    HttpResponse::Ok().json(ProductList::list())
}