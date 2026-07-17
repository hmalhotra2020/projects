const redis = require('redis')
const stockdata = require('node-stock-data');
var { DateTime } = require('luxon');
var axios = require('axios');
var _ = require("underscore");

const bluebird = require('bluebird');
const client = redis.createClient();

bluebird.promisifyAll(redis);

var controllers = {
    getSessionSymbols: async function(req, res) {
        if (client != null && !client.isOpen)   {
            await client.connect();
        }
        var reply = await client.SMEMBERS("TICKERS");
        if(reply)   {
            res.send(reply);
        } else res.send([]);
    },
    getCurrentPrice: async function(req, res) {
        var symbols = req.body.symbols
        symbols = (symbols && _.isArray(symbols)) ? symbols : _.toArray(symbols);
        //console.log('symbols ', symbols, _.isArray(symbols))

        var symbolsArray = symbols;
        var results = [];
        var tmpArray = [];

        if (client != null && !client.isOpen)   {
            await client.connect();
        }

        for (let symbol of symbolsArray) {            
            let reply = await client.HGET("realtime", symbol);
            if (reply)
                results.push(JSON.parse(reply));
            else
                tmpArray.push(symbol);
        };
        
        if(tmpArray.length > 0) {
            var realtimeData = await getRealtimeData(tmpArray);
            for (var iPos=0; iPos<realtimeData.data.length; iPos++)    {
                var response = realtimeData.data[iPos];
                var symbol = response.symbol;
                var responseStr = JSON.stringify(response);
                await client.hSet("realtime", symbol, responseStr);
                results.push(response);
            }
        }
        
        res.send(results);
        
    },
    getHistoricalData: async function(req, res)   {
        var symbol = req.query.symbol;
        var date_to = DateTime.local();
        var date_from = date_to.minus({days: 20}).toFormat('yyyy-MM-dd');
        maxDate = date_to.toFormat('yyyy-MM-dd');

        if (client != null && !client.isOpen)   {
            await client.connect();
        }
        //console.log('eod ', symbol, date_from, maxDate)
        var reply = await client.hGet("history", symbol);
        //console.log('reply getHistoricalData ', reply)
        if(!reply)  {
            var eodData = await getOldData(symbol, date_from, maxDate);
            client.hSet("history", symbol, JSON.stringify(eodData));
            res.send(eodData)
        }
        else res.send(reply)
    }
}

async function getRealtimeData(symbolsArray)  {

    if( !symbolsArray || symbolsArray.length == 0 )
        return [];

    var url = 'http://api.marketstack.com/v1/intraday/latest?exchange=XNAS&symbols=' + symbolsArray.join(',');
    url += '&access_key=' + process.env.TOKEN;
    console.log('Calling market URL: ', url)
    var result = await axios.get(url);
    return result.data;
}

async function getOldData(symbols, date_from, date_to)  {

    if( !symbols || symbols.length == 0 )
        return [];

    var url = 'http://api.marketstack.com/v1/eod?&date_from=' + date_from + '&date_to=' + date_to + '&symbols=' + symbols;
    url += '&access_key=' + process.env.TOKEN;
    console.log('Calling market URL: ', url)
    var result = await axios.get(url);
    return result.data;
}

module.exports = controllers;
