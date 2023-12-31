#  Base de Datos Distribuida
## 📄 Descripcion:

<p>En este proyecto se simula el concepto basico de una de base de datos distribuida. El sistema consta de una base de datos global dividida en 3 repositorios, un servidor y un cliente. Tambien se toma en cuenta lo siguiente:</p>

- El servidor generara indefinidamente n < 16 CURPS por segundo para ir ingresandolos a la base de datos en un repositorio que se encuentre disponible.
- El menu contara con las siguientes opciones:</br>
  1 - CURPs por segundo que se generan.<br/>
  2 - Registros totales en la base de datos.<br/>
  3 - Registros en cada repositorio.<br/>
  4 - Bytes de la base de datos y cada repositorio individual.<br/>
  5 - Total de hombres y mujeres en la base de datos.<br/>
  6 - Registros que existen para una entidad.<br/>
- Las entidad federativa de México son: [AS, BC, BS, CC, CS,CH, CL, CM, DF, DG,GT, GR, HG, JC, MC, MN, MS, NT, NL, OC, PL,QT, QR, SP, SL, SR, TC, TL, TS, VZ, YN, ZS]
- Los repositorios almacenaran los CURPS en memoria de programa.
- NOTA: Esto es meramente practico ya que la capacidad de almacenamiento sera igual al total de memoria disponible en la pc</br>

### 📊 Esquema
<img width="600" heigth="600" src="img/db_distributed.png" alt="Base de Datos">

---
## 🖥️ Tecnologías Utilizadas:
- Lenguaje: Java ☕
- Sistema Operativo: Linux (Ubuntu) 🐧
  
---
## 📱 Calculos
- Los n < 16 CURPS se calcularon en base al promedio en que el programa generador creo CURPS aleatoriamente en un segundo.<br>
  Nota: Se utilizo un procesador Intel Core i7, el tiempo puede variar dependiendo el procesador.
- Para calcular el peso de un CURP se guardo en un .txt y dio como resultado un peso de aproximadamente 19 Bytes.

---
## 🖥 Vistas
### 🔹 Menu - Opciones
<img width="600" heigth="600" src="img/Menu.png" alt="Menu1">
<img width="600" heigth="600" src="img/Opcion1234.png" alt="Menu2">
<img width="600" heigth="600" src="img/Opcion567.png" alt="Menu3">
### 🔹 Generador
<img width="600" heigth="600" src="img/Generador.png" alt="Generador">
### 🔹 Repositorios
<img width="600" heigth="600" src="img/Servidor1.png" alt="Repo1">
<img width="600" heigth="600" src="img/Servidor2.png" alt="Repo2">
<img width="600" heigth="600" src="img/Servidor3.png" alt="Repo3">
