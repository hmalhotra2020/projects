var express = require('express');
var router = express.Router();
const redis = require('redis')
const client = redis.createClient();

router.get('/', async function (req, res, next) {
    if (client != null && !client.isOpen) {
        await client.connect();
    }
    var symbols = await client.SMEMBERS("TICKERS");
    if (symbols) {
        res.send(symbols)
    } else {
        res.send([])
    }

});

router.post('/', async function (req, res) {
    var symbol = req.body.symbol;

    if (client != null && !client.isOpen) {
        await client.connect();
    }

    var doesExist = await client.SISMEMBER("TICKERS", symbol);
    if (!doesExist || doesExist <= 0) {
        await client.SADD("TICKERS", symbol);
    }
    var allAddedSymbols = await client.SMEMBERS("TICKERS");
    if (allAddedSymbols) {
        res.send(allAddedSymbols);
    }
    else res.send([])
});

module.exports = router;
