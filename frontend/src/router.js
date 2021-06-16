
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import StoreManager from "./components/StoreManager"
import MenuManager from "./components/MenuManager"

import OrderManager from "./components/OrderManager"

import PaymentManager from "./components/PaymentManager"

import MsgManager from "./components/MsgManager"

import ReviewManager from "./components/ReviewManager"

import Mypage from "./components/Mypage"
import DeliveryManager from "./components/DeliveryManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/Store',
                name: 'StoreManager',
                component: StoreManager
            },
            {
                path: '/Menu',
                name: 'MenuManager',
                component: MenuManager
            },

            {
                path: '/Order',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/Payment',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/Msg',
                name: 'MsgManager',
                component: MsgManager
            },

            {
                path: '/Review',
                name: 'ReviewManager',
                component: ReviewManager
            },

            {
                path: '/Mypage',
                name: 'Mypage',
                component: Mypage
            },
            {
                path: '/Delivery',
                name: 'DeliveryManager',
                component: DeliveryManager
            },



    ]
})
