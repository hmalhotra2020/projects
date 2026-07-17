var express = require('express');
var router = express.Router();

var configRouter = require('./config/config');
var searchRouter = require('./search/search');
var stocksRouter = require('./stocks/stocks');

router.use('/search', searchRouter);
router.use('/config', configRouter);

router.get('/stocks', stocksRouter.getHistoricalData);
router.post('/stocks/price', stocksRouter.getCurrentPrice);
router.get('/stocks/selected', stocksRouter.getSessionSymbols);

module.exports = router;
