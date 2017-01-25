module.exports = {
    entry: './src/index.js',
    output: {
    filename: 'bundle.js',
    path: '../public/js'
},
module: {
    loaders: [
        {
            test: /\.js$/,
            exclude: /node_modules/,
            loader: "babel-loader",
            query: {
                presets: ['es2015', 'react']
            }
        }
    ]
    }
}
