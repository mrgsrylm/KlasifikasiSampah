//import mongoose
const mongoose = require('mongoose')

// import the model we just created
// BlogPost represents the BlogPosts collection in the database
const BlogPost = require('./src/models/BlogPost')

// if my_database doesn't exist, it will be created for us
mongoose.connect('mongodb://localhost/my_database', {useNewUrlParser: true});

// to create a new BlogPost doc in our database, we will use 
// a function in our model called create

BlogPost.create({
    //author: ObjectId,
    title: 'The Mythbuster’s Guide to Saving Money on Energy Bills',
    desc: 'Once you get past the beginner-level energy-saving stuff, a whole new world of thrifty nerdery opens up. Here are some secrets to copping a load of money off your utilities bills. ',    
}, (error, blogpost) =>{
    console.log(error,blogpost)
})
