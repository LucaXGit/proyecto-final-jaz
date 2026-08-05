<?php

return [

    /*
    |--------------------------------------------------------------------------
    | Third Party Services
    |--------------------------------------------------------------------------
    */

    'postmark' => [
        'key' => env('POSTMARK_API_KEY'),
    ],

    'resend' => [
        'key' => env('RESEND_API_KEY'),
    ],

    'ses' => [
        'key' => env('AWS_ACCESS_KEY_ID'),
        'secret' => env('AWS_SECRET_ACCESS_KEY'),
        'region' => env('AWS_DEFAULT_REGION', 'us-east-1'),
    ],

    'slack' => [
        'notifications' => [
            'bot_user_oauth_token' => env('SLACK_BOT_USER_OAUTH_TOKEN'),
            'channel' => env('SLACK_BOT_USER_DEFAULT_CHANNEL'),
        ],
    ],

    /*
    |--------------------------------------------------------------------------
    | Backend Java - Tienda de Playeras
    |--------------------------------------------------------------------------
    */

    'productos' => [
        'url' => env(
            'PRODUCTOS_API_URL',
            'http://backend:8080/ServidorTiendaPlayeras/ProductoServlet'
        ),
    ],

    'usuarios' => [
        'url' => env(
            'USUARIOS_API_URL',
            'http://payara-container:8080/ServidorTiendaPlayeras/api/auth'
        ),
    ],

    'users' => [
        'url' => env(
            'USERS_API_URL',
            'http://payara-container:8080/ServidorTiendaPlayeras/api/users'
        ),
    ],

    'carrito' => [
        'url' => env(
            'CARRITO_API_URL',
            'http://payara-container:8080/ServidorTiendaPlayeras/api/carrito'
        ),
    ],

    'ordenes' => [
        'url' => env(
            'ORDENES_API_URL',
            'http://payara-container:8080/ServidorTiendaPlayeras/api/ordenes'
        ),
    ],

];