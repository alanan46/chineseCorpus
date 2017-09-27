import os
import re
import json
import datetime
from libs.pprint import pprint
from pyspider.libs.base_handler import *


class Handler(BaseHandler):
    crawl_config = {
        
    }

    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://tieba.baidu.com/f?ie=utf-8&kw=经济&fr=search', callback=self.index_page)

    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):

        for each in response.doc('.j_threadlist_bright > .clearfix .j_threadlist_li_left > span').items():
            self.crawl(each.attr.href,callback=self.detail_page)
   
        for each in response.doc('.j_threadlist_bright > .clearfix .j_threadlist_li_left > span').items():
           ## pattern = re.compile(r'dir_\d+_short') 
           # match = pattern.search(each.attr.id)
           # print match
            #if (each.attr.id !=None):
               print each.text()
        next = response.doc('.next').attr.href    
        self.crawl(next, callback=self.index_page)    

    @config(priority=2)
    def detail_page(self, response):
        list = []
        list = response.doc('.j_threadlist_bright > .clearfix .j_threadlist_li_left > span').text
#        print(response.text)
        for i in img :
            img_list.append(i)
        list = list(set(list))
        return {
            "url": response.url,
            “list” : list,
        }