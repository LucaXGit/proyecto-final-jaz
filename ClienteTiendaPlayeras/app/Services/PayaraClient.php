<?php

namespace App\Services;

use Illuminate\Http\Client\ConnectionException;
use Illuminate\Http\Client\Response;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class PayaraClient
{
    /**
     * Realiza una petición HTTP controlada al backend de Payara.
     */
    public static function request(string $method, string $url, array $data = [], ?string $token = null): array
    {
        try {
            $request = Http::acceptJson()->timeout(10);

            if ($token) {
                $request = $request->withToken($token);
            }

            $response = match (strtolower($method)) {
                'post' => $request->asJson()->post($url, $data),
                'put' => $request->asJson()->put($url, $data),
                'delete' => $request->delete($url, $data),
                default => $request->get($url, $data),
            };

            return self::handleResponse($response);
        } catch (ConnectionException $e) {
            Log::error("Payara connection error calling {$url}: " . $e->getMessage());
            return [
                'success' => false,
                'message' => 'No se pudo establecer conexión con el servidor interno de Metro Drop.',
                'code' => 504,
                'errors' => [],
            ];
        }
    }

    /**
     * Procesa la respuesta de la petición HTTP estandarizando la estructura de datos.
     */
    private static function handleResponse(Response $response): array
    {
        $status = $response->status();
        $data = $response->json();

        if ($response->successful()) {
            return array_merge([
                'success' => true,
                'code' => $status,
            ], is_array($data) ? $data : []);
        }

        $message = $data['message'] ?? 'Ocurrió un error inesperado al procesar la solicitud.';

        return [
            'success' => false,
            'message' => $message,
            'errors' => $data['errors'] ?? [],
            'code' => $status,
        ];
    }
}
