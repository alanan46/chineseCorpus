#!/usr/bin/env python
# -- encoding: utf-8 --
# Created on 2017-09-19 20:14:15
# Project: 007

from pyspider.libs.base_handler import *


class Handler(BaseHandler):
    crawl_config = {
    }

    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://tieba.baidu.com/f?ie=utf-8&kw=经济学&fr=search', callback=self.index_page)

    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):

        for each in response.doc('.j_threadlist_bright > .clearfix .j_th_tit > a').items():
            self.crawl(each.attr.href,callback=self.detail_page)
            
        next = response.doc('.next').attr.href    
        self.crawl(next, callback=self.index_page)    

    @config(priority=2)
    def detail_page(self, response):

        return {
            "url": response.url
        }        
