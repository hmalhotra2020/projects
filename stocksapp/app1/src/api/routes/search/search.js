var express = require('express');
var router = express.Router();
var redisMiddleware = require('../../middleware/cache')
var axios = require('axios');
var _ = require("underscore");
require('express-async-errors');
const redis = require('redis')
const client = redis.createClient();

//router.get('/', redisMiddleware, async function(req, res, next) {
router.get('/', async function (req, res, next) {
    var symbol = req.query.symbol;

    let key = "info_" + symbol;
    if (client != null && !client.isOpen) {
        await client.connect();
    }
    const reply = await client.get(key);
    
    if (reply) {
        res.send(JSON.parse(reply));
    } else {
        var url = 'https://api.marketstack.com/v1/tickers?search=' + symbol + '&exchange=XNAS';
        url += '&access_key=' + process.env.TOKEN;
        console.log('Calling market URL: ', url)
        var result = await axios.get(url);
        client.set(key, JSON.stringify(result.data));
        res.send(result.data);
    }
    
});

module.exports = router;
