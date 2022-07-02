const mongoose = require('mongoose')

const BlogPost = require('./src/models/BlogPost')

mongoose.connect('mongodb://localhost/my_database',{useNewUrlParser: true});

var id = "605d16b1b0e8f94779dacaea"

BlogPost.findByIdAndDelete(id,{
    title:'updated title'
},(error,blogspot)=>{
    console.log(error,blogspot)
})