# Scripts del backend

Los scripts de operacion del proyecto viven en la carpeta raiz `../scripts/`:

- `iniciar-mysql.ps1`: arranca la instancia MySQL local en `127.0.0.1:3308`.
- `detener-mysql.ps1`: detiene esa instancia.
- `ejecutar.ps1`: arranca MySQL, verifica la base y ejecuta el backend.
- `smoke-test.ps1`: prueba login, seguridad y endpoints principales.

`api.http` es una coleccion manual para VS Code REST Client / IntelliJ HTTP Client.
Recuerde ejecutar el login y pegar el token en la variable `@token`.
