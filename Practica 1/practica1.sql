USE master
GO

set nocount on
go

IF DB_ID (N'P101') IS NOT NULL
BEGIN
	ALTER DATABASE P101 SET  SINGLE_USER WITH ROLLBACK IMMEDIATE
	DROP DATABASE P101;
END
GO
CREATE DATABASE P101
GO
USE P101
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- The below scipt enables the use of In-Memory OLTP in the current database, 
--   provided it is supported in the edition / pricing tier of the database.
-- It does the following:
-- 1. Validate that In-Memory OLTP is supported. 
-- 2. In SQL Server, it will add a MEMORY_OPTIMIZED_DATA filegroup to the database
--    and create a container within the filegroup in the default data folder.
-- 3. Change the database compatibility level to 130 (needed for parallel queries
--    and auto-update of statistics).
-- 4. Enables the database option MEMORY_OPTIMIZED_ELEVATE_TO_SNAPSHOT to avoid the 
--    need to use the WITH (SNAPSHOT) hint for ad hoc queries accessing memory-optimized
--    tables.
--
-- Applies To: SQL Server 2016 (or higher); Azure SQL Database
-- Author: Jos de Bruijn (Microsoft)
-- Last Updated: 2016-05-02

SET NOCOUNT ON;
SET XACT_ABORT ON;

-- 1. validate that In-Memory OLTP is supported
IF SERVERPROPERTY(N'IsXTPSupported') = 0 
BEGIN                                    
    PRINT N'Error: In-Memory OLTP is not supported for this server edition or database pricing tier.';
END 
IF DB_ID() < 5
BEGIN                                    
    PRINT N'Error: In-Memory OLTP is not supported in system databases. Connect to a user database.';
END 
ELSE 
BEGIN 
	BEGIN TRY;
-- 2. add MEMORY_OPTIMIZED_DATA filegroup when not using Azure SQL DB
	IF SERVERPROPERTY('EngineEdition') != 5 
	BEGIN
		DECLARE @SQLDataFolder varchar(max) = cast(SERVERPROPERTY('InstanceDefaultDataPath') as varchar(max))
		DECLARE @MODName varchar(max) = DB_NAME() + N'_mod';
		DECLARE @MemoryOptimizedFilegroupFolder varchar(max) = @SQLDataFolder + @MODName;

		DECLARE @SQL varchar(max) = N'';

		-- add filegroup
		IF NOT EXISTS (SELECT 1 FROM sys.filegroups WHERE type = N'FX')
		BEGIN
		ALTER DATABASE CURRENT  SET AUTO_CLOSE OFF
			SET @SQL = N'
ALTER DATABASE CURRENT 
ADD FILEGROUP ' + QUOTENAME(@MODName) + N' CONTAINS MEMORY_OPTIMIZED_DATA;';
			EXECUTE (@SQL);

		END;

		-- add container in the filegroup
		IF NOT EXISTS (SELECT * FROM sys.database_files WHERE data_space_id IN (SELECT data_space_id FROM sys.filegroups WHERE type = N'FX'))
		BEGIN
			SET @SQL = N'
ALTER DATABASE CURRENT
ADD FILE (name = N''' + @MODName + ''', filename = '''
						+ @MemoryOptimizedFilegroupFolder + N''') 
TO FILEGROUP ' + QUOTENAME(@MODName);
			EXECUTE (@SQL);
		END
	END

	-- 3. set compat level to 130 if it is lower
	IF (SELECT compatibility_level FROM sys.databases WHERE database_id=DB_ID()) < 130
		ALTER DATABASE CURRENT SET COMPATIBILITY_LEVEL = 130 

	-- 4. enable MEMORY_OPTIMIZED_ELEVATE_TO_SNAPSHOT for the database
	ALTER DATABASE CURRENT SET MEMORY_OPTIMIZED_ELEVATE_TO_SNAPSHOT = ON;


    END TRY
    BEGIN CATCH
        PRINT N'Error enabling In-Memory OLTP';
		IF XACT_STATE() != 0
			ROLLBACK;
        THROW;
    END CATCH;
END;
go
--Creación de tablas

create table perfil (
	perfil char(3) primary key,
	descripcion varchar(20)
);

create table usuario (
    usuId int IDENTITY(1,1) constraint PK_usuario primary key,
	password varbinary(64),
    apodo varchar(15),
	email varchar(254) not null,
	nombre varchar(50) not null,
	apellidos varchar(75) not null,
	nacido date not null,
	perfil char(3) default '0',
	constraint FK_usuario_perfil foreign key (perfil) references perfil(perfil)
);

create table conexion (
	numconex int IDENTITY(1,1) constraint PK_conexion primary key,
	usuId int,
	entra datetime,
	constraint FK_conexion_usuario foreign key (usuId) references usuario(usuId)
);

create table sigue (
	usuId int,
	siguea int,
	constraint PK_sigue primary key (usuId,siguea),
	foreign key (usuId) references usuario(usuId),
	foreign key (siguea) references usuario(usuId)
);

create table comentario (
	usuId int,
	numcom smallint,
	comenta varchar(1000) not null,
	respondea int,
	respondeanum smallint,
	cuando datetime2,
	constraint PK_comentario primary key (usuId,numcom),
	constraint FK_comentario_usuario foreign key (usuId) references usuario(usuId),
	constraint FK_comentario_comentario foreign key (respondea,respondeanum) references comentario(usuId,numcom)
);

create table palabrasclave (
	keyw varchar(25) constraint PK_palabrasclave primary key
); 

create table comkeyw (
	keyw varchar(25),
	usuId int,
	numcom smallint,
	constraint PK_comkeyw primary key (keyw,usuId,numcom),
	constraint FK_comkeyw_palabrasclave foreign key (keyw) references palabrasclave(keyw),
	constraint FK_comkeyw_comentario foreign key (usuId,numcom) references comentario(usuId,numcom)
);

create table valora (
	numVal bigint IDENTITY(1,1) constraint PK_valora primary key,
	usuId int,
	valorado int,
	numcom smallint,
	estrellas tinyint constraint CHK_estrellas_valor check (estrellas >0 and estrellas <6),
	constraint FK_valora_usuario foreign key (usuId) references usuario(usuId),
	constraint FK_valora_comentario foreign key (valorado,numcom) references comentario(usuId,numcom)
);

GO

-- insertando datos estáticos
insert into perfil values ('0','Básico'),('1','Estándar'),('2','Premium');

GO

--select * from perfil;

-- datos temporales para generación aleatoria
DROP TABLE IF EXISTS #apellido;
DROP TABLE IF EXISTS #nombre;
DROP TABLE IF EXISTS #correo;
create table #apellido (apellido varchar(75));
create table #nombre (nombre varchar(50));
create table #correo (correo varchar(254));
go

insert into #apellido values ('Andreo'),('Adánez'),('Alejándrez'),('Álvarez'),('Alves'),('Ansúrez'),('Antolínez'),('Antúnez'),('Arranz'),('Aznárez'),('Báez'),('Baz'),('Benéitez'),('Benítez'),('Bermúdez'),('Bernárdez'),('Blánquez'),('Blázquez'),('Briz'),('Chaves'),('Chávez'),('Díaz'),('Diéguez'),('Díez'),('Díez'),('Diz'),('Domínguez'),('Enríquez'),('Escámez'),('Estébanez'),('Estévez'),('Félez'),('Fernandes'),('Fernández'),('Ferrandis'),('Ferrándiz'),('Ferris'),('Férriz'),('Fortúnez'),('Froilaz'),('Galíndez'),('Gálvez'),('Gámez'),('Gámiz'),('Garcés'),('García'),('Gásquez'),('Gelmírez'),('Giménez'),('Giráldez'),('Gomáriz'),('Gomes'),('Gómez'),('Gomis'),('Gonzales'),('González'),('Gonzálvez'),('Gutiérrez'),('Henríquez'),('Hernáez'),('Hernáiz'),('Hernández'),('Hernanz'),('Herráez'),('Herraiz'),('Herranz'),('Ibáñez'),('Íñiguez'),('Jáñez'),('Jiménez'),('Juárez'),('Júlvez'),('Laínez'),('Lendínez'),('Lopes'),('López'),('Lupiañez'),('Márques'),('Márquez'),('Martínez'),('Matesanz'),('Meléndez'),('Mendes'),('Méndez'),('Menéndez'),('Migueláñez'),('Miguélez'),('Míguez'),('Mínguez'),('Muñiz'),('Muñoz'),('Narváez'),('Nunes'),('Núñez'),('Ordóñez'),('Ortiz'),('Páez'),('Peláez'),('Pérez'),('Periáñez'),('Peribáñez'),('Peris'),('Piris'),('Piriz'),('Póbez'),('Quiles'),('Quílez'),('Raimúndez'),('Ramírez'),('Rísquez'),('Rodrigues'),('Rodríguez'),('Ruipérez'),('Ruiz'),('Rupérez'),('Rus'),('Ruz'),('Sáenz'),('Sáez'),('Sáinz'),('Sáiz'),('Salvadórez'),('Sánchez'),('Sanchís'),('Sánchiz'),('Sanz'),('Saz'),('Segúndez'),('Suárez'),('Téllez'),('Valdés'),('Valdez'),('Vásquez'),('Vaz'),('Vázquez'),('Velásquez'),('Velázquez'),('Vélez'),('Véliz'),('Viéitez'),('Viúdez'),('Ximénez'),('Suárez'),('Yágüe'),('Yágüez'),('Yanes'),('Yánez'),('Yáñez');
insert into #nombre values ('Antonio'),('Jose'),('Manuel'),('Francisco'),('Juan'),('David'),('Jose Antonio'),('Jose Luis'),('Javier'),('Francisco Javier'),('Jesus'),('Daniel'),('Carlos'),('Miguel'),('Alejandro'),('Jose Manuel'),('Rafael'),('Pedro'),('Angel'),('Miguel Angel'),('Jose Maria'),('Fernando'),('Pablo'),('Luis'),('Sergio'),('Jorge'),('Alberto'),('Juan Carlos'),('Juan Jose'),('Alvaro'),('Diego'),('Adrian'),('Juan Antonio'),('Raul'),('Enrique'),('Ramon'),('Vicente'),('Ivan'),('Ruben'),('Oscar'),('Andres'),('Joaquin'),('Juan Manuel'),('Santiago'),('Eduardo'),('Victor'),('Roberto'),('Jaime'),('Francisco Jose'),('Mario'),('Ignacio'),('Alfonso'),('Salvador'),('Ricardo'),('Marcos'),('Jordi'),('Emilio'),('Julian'),('Julio'),('Guillermo'),('Gabriel'),('Tomas'),('Agustin'),('Jose Miguel'),('Marc'),('Gonzalo'),('Felix'),('Jose Ramon'),('Mohamed'),('Hugo'),('Joan'),('Ismael'),('Nicolas'),('Cristian'),('Samuel'),('Mariano'),('Josep'),('Domingo'),('Juan Francisco'),('Aitor'),('Martin'),('Alfredo'),('Sebastian'),('Jose Carlos'),('Felipe'),('Hector'),('Cesar'),('Jose Angel'),('Jose Ignacio'),('Victor Manuel'),('Iker'),('Gregorio'),('Luis Miguel'),('Alex'),('Jose Francisco'),('Juan Luis'),('Rodrigo'),('Albert'),('Xavier'),('Lorenzo ');
insert into #correo values ('gmail.com'),('hotmail.com'),('outlook.com'),('alu.ua.es'),('ua.es'),('gcloud.ua.es'),('mscloud.ua.es');
go

-- palabras clave
insert into palabrasclave values ('albacete'),('gratis'),('barato'),('empleo'),('.doc'),('alicante'),('gratuito'),('barata'),('empleos'),('.docx'),('almeria'),('gratuita'),('economico'),('trabajo'),('.docm'),('alava'),('gratuitamente'),('economica'),('trabajos'),('.pages'),('asturias'),('RRSS'),('promocion'),('trabajar'),('.pdf'),('avila'),('facebook'),('oferta'),('practica'),('.pub'),('badajoz'),('fb'),('low'),('cost'),('practicas'),('.txt'),('baleares'),('instagram'),('asequible'),('rrhh'),('.xml'),('barcelona'),('insta'),('rebaja'),('recursos'),('humanos'),('.xps'),('bizkaia'),('twitter'),('rebajas'),('cv'),('doc'),('burgos'),('rebajado'),('vitae'),('docx'),('caceres'),('snapchat'),('rebajada'),('infojobs'),('docm'),('cadiz'),('youtube'),('ganga'),('trabajando'),('pages'),('cantabria'),('pinterest'),('ocasión'),('linkedin'),('pdf'),('castellon'),('slideshare'),('descuento'),('job'),('pub'),('ciudad'),('real'),('OTROS'),('descuentos'),('jobs'),('txt'),('cardoba'),('video'),('CONSULTAS'),('beca'),('xml'),('coruña'),('videos'),('que es'),('becario'),('xps'),('cuenca'),('foto'),('como'),('becas'),('.csv'),('gipuzkoa'),('fotografia'),('quien'),('master'),('.xls'),('girona'),('fotografias'),('traduccion'),('masters'),('csv'),('granada'),('imagen'),('traductor'),('universidad'),('xls'),('guadalajara'),('imágenes'),('ingles'),('universitario'),('.ai'),('huelva'),('fotolia'),('english'),('universitaria'),('ai'),('huesca'),('groupon'),('frances'),('universitarios'),('.azw'),('jaen'),('blog'),('french'),('universitarias'),('.azw3'),('leon'),('blogs'),('aleman'),('curso'),('.epub'),('lleida'),('foro'),('german'),('cursos'),('.lit'),('lugo'),('foros'),('opinion'),('tutorial'),('.mobi'),('madrid'),('portal'),('opiniones'),('libro'),('azw'),('malaga'),('portales'),('definicion'),('libros'),('azw3'),('murcia'),('sexo'),('tutoriales'),('epub'),('navarra'),('sexual'),('significado'),('clase'),('lit'),('ourense'),('sexuales'),('sinonimo'),('clases'),('mobi'),('palencia'),('porno'),('sinonimos'),('estudio'),('.gif'),('palmas'),('pornografia'),('antonimo'),('estudios'),('.ico'),('pontevedra'),('xxx'),('antonimos'),('asignatura'),('.jpeg'),('rioja'),('podcast'),('wikipedia'),('asignaturas'),('.jpg'),('salamanca'),('ebook'),('wiki'),('proyecto'),('.png'),('juancarballo.com'),('tenerife'),('contacto'),('traducir'),('proyectos'),('.psd'),('segovia'),('contactar'),('concepto'),('.tif'),('sevilla'),('email'),('conceptos'),('gif'),('soria'),('mail'),('ico'),('tarragona'),('telefono'),('inventor'),('jpeg'),('teruel'),('telefonos'),('invento'),('jpg'),('toledo'),('segunda mano'),('manual'),('png'),('valencia'),('ebay'),('manuales'),('psd'),('valladolid'),('amazon'),('instruccion'),('tif'),('zamora'),('shopping'),('instrucciones'),('.key'),('zaragoza'),('milanuncios'),('saber'),('.pps'),('ceuta'),('segundamano'),('dossier'),('.ppsx'),('melilla'),('descargar'),('dosier'),('.ppt'),('descargas'),('dossieres'),('.pptm'),('dosieres'),('.pptx'),('portfolio'),('key'),('portfolios'),('pps'),('presentacion'),('ppsx'),('presentaciones'),('ppt'),('comparar'),('pptm'),('comparador'),('pptx'),('guia'),('.avi'),('guias'),('.divx'),('ejemplo'),('.mov'),('ejemplos'),('.mp4'),('herramienta'),('.mpeg'),('herramientas'),('.mpg'),('casero'),('.wmv'),('caseros'),('avi'),('remedios'),('divx'),('remedio'),('mov'),('a mano'),('mp4'),('en casa'),('mpeg'),('lista'),('mpg'),('listado'),('wmv');
go

--select * from #apellido;
--select * from #nombre;
--select * from #correo;


-- Para generar fechas aleatorias------------------------------------------------------------
-- Visto en https://misalgoritmosnet.wordpress.com/2013/07/24/fecha-aleatoria-sql-server/
drop view  if exists seeder
go
CREATE VIEW seeder
AS
    SELECT RAND(CONVERT(VARBINARY, NEWID())) seed
GO


drop fUNCTION if exists getRandomDate
go
CREATE FUNCTION getRandomDate(@lower DATE,@upper DATE)
RETURNS DATE
AS
	BEGIN
		DECLARE @random DATE
		SELECT @random = DATEADD(day, DATEDIFF(DAY, @lower, @upper) * seed, @lower) from seeder
		RETURN @random
	END
go
---------------------------------------------------------------------------------------------------

CREATE PROCEDURE generar_usuarios @numusuarios int

as 
--Declaracion de las variables

declare @nombre varchar(50)
declare @apellidos varchar(50)
declare @usuarioEmail varchar(100)
declare @servidor varchar(50)
declare @contador int
declare @apellido1 varchar(25)
declare @posicionEspacio int
declare @fechaNac date

--Creamos la tabla donde vamos a guardar los datos de nombre y apellidos
--Esta tabla sera temporal 

DROP TABLE IF EXISTS #nombreApellidos
create table #nombreApellidos (orden int,nombre varchar(50),apellidos varchar(75))

--drop table #nombreApellidos

--Se generaran ahora los datos que almacenaremos dentro de la tabla

INSERT INTO #nombreApellidos select row_number() over(order by newid()) orden, nombre, a1.apellido + ' ' + a2.apellido from  #nombre n ,#apellido a1, #apellido a2;

--select * from #nombreApellidos

--Creamos el bucle donde introduciremos los usuarios dependiendo del numero introducido en el procedimiento

set @contador = 1

while @contador <= @numusuarios
begin

--Obtenemos la fecha aleatoria de nacimiento del usuario entre los siguientes valores '1960-01-01' y '2019-10-11'

set @fechaNac = dbo.getRandomDate('1960-01-01', '2019-10-11');

--Obtenemos valores aleatorios para nombre y apellido

select top(1) @nombre = nombre from #nombreApellidos order by newid();
select top(1) @apellidos = apellidos from #nombreApellidos order by newid();

--Obtenemos la posicion donde se encuentra el espacio para separar los dos apellidos y obtener el primero
set @posicionEspacio = CHARINDEX ( ' ', @apellidos , 0 ) 
--Separamos la cadena
set @apellido1 = SUBSTRING( @apellidos ,0 , @posicionEspacio )  

--print @apellidos;
--print @apellido1;
--print @nombre;

--seleccionar 1 servidor de correo aleatorio: (@serv)

select top 1 @servidor='@'+correo from #correo order by newid();

--El dato de correo electrónico se compondrá de nombre.primerapellido@servidor, 
--siendo servidor un valor extraído aleatoriamente de la tabla temporal creada en el script anterior.

set @usuarioEmail = @nombre + '.' + @apellido1 + @servidor

--insertar en USUARIO (email,nombre,apellidos,nacido) = (@email,@nom,@apes,fecha_aleatoria

INSERT into usuario (email,nombre,apellidos,nacido) values (@usuarioEmail,@nombre,@apellidos,@fechaNac);

set @contador = @contador + 1 

end

--Pruebas

	--EXEC numero_usuarios 10;  
		--Para 1000 usuarios tiempo de creacion = 15 min aprox
	--select * from usuario


	--Se trata de rellenar unas tablas con datos aleatorio, algunos extraídos de otras tablas.
	--Debes encapsularlo en procedimientos almacenados, con uno de ellos como el principal que llama a todos los demás.
	--Aquí se describe cómo ha de ser la base de datos. Para los datos aleatorios puedes utilizar SQL dinámico.


--	Los requisitos
--Se asignará perfil 1 al 20 % de los usuarios, y perfil 2 al 5%.
--Los usuarios pueden seguir a entre 0 y 10 otros usuarios. 
--Un 25% de los usuarios realizan entre 1 y 5 comentarios; el texto del comentario puede ser una constante para todos ellos. Los dos valores anteriores (0.25 y 5) se pasarán como parámetro. 
--Un 30% de los comentarios son respuestas a otros comentarios.
--Un 25% de los comentarios estarán valorados; el 5% de los usuarios hace esas valoraciones. 
--Cada comentario tendrá entre 0 y 4 palabras clave. 
--Se almacenarán entre 1 y 3 conexiones por usuario.


--Modificar perfiles

CREATE PROCEDURE modificarPerfiles

as
--Declaramos las variables
declare @perfil char(3), @cantidad int, @cantidad_20 int, @cantidad_5 int;

--Determinar la cantidad de usuarios actual
set @cantidad = (select count(*) from usuario)
--Determinar el 20% y el 5% de usuarios actual
set @cantidad_20 = (SELECT CAST((ROUND(@cantidad*.50, 0, 0)) AS INT))
set @cantidad_5 = (SELECT CAST((ROUND(@cantidad*.40, 0, 0)) AS INT));

--Si, de estos, queremos el 20% —de cantidad—, usaremos top().
	--select top(@cantidad_20) usuid,perfil from usuario order by newid();
	--select top(@cantidad_5) usuid,perfil from usuario order by newid();

--Modificar perfil para las filas indicadas en el select del 20% y mantener esta consulta para que las del 5% sean diferentes a las del 20%
WITH cons_temp 
AS (select top(@cantidad_20) usuid,perfil from usuario order by newid())
update cons_temp set perfil=1;
WITH cons_temp2
AS 
(select top(@cantidad_5) usuid,perfil from usuario where perfil = 0 order by newid() )
update cons_temp2 set perfil=2;

--select usuid,perfil from usuario order by newid();
--update usuario set perfil = 0;

go

--exec modificarPerfiles;


--Que @usu1 y @usu2 almacenen usuId reales
--Los usuarios pueden seguir a entre 0 y 10 otros usuarios. 
create procedure gen_sigue

as
declare @cuantosTotal int, @usu1 int, @usu2 int , @cuantos int;

select @cuantosTotal = count(*) from usuario;
set @cuantos = @cuantosTotal;
select @usu1=floor(rand()*2)+1;
select @usu2=floor(rand()*2)+1;

while (@cuantos > 0)	
begin
	set @usu1=1;
	set @usu2=1;
	while (@usu1 = @usu2)
	begin
		select @usu1=floor(rand()*@cuantosTotal)+1;
		select @usu2=floor(rand()*@cuantosTotal)+1;
	end
	--Si existe algun duplicado se reiniciara el bucle y se buscaras diferentes usuarios.
	IF (select count(*) from sigue where usuId = @usu1 AND siguea = @usu2) = 0 	
	begin
	insert into sigue (usuId,siguea) values(@usu1,@usu2);
	set @cuantos=@cuantos-1;
	end

END
go

--select * from sigue
--DELETE FROM sigue


--Generación de comentarios
--select * from comentario
CREATE PROCEDURE gen_comenta @numUsuariosComentario int, @maxPorUsu int

AS
--Declarar variables
DECLARE @contador int, @cuantos int,@usuId int, @numCom int, @comenta varchar(100), @cuando date;

--Declarar cursor sobre @numComentarios identificadores de usuario
DECLARE cursorComentario CURSOR FOR select usuId from usuario WHERE usuId < @numUsuariosComentario+1;
--Abrir cursor
OPEN cursorComentario

	FETCH NEXT FROM cursorComentario INTO @usuId
	WHILE @@FETCH_STATUS = 0
	BEGIN
	set @contador = 0;
	set @comenta = 'Esto es un comentario';
		--Mientras que el contador sea menor que el maximo comentarios por usuario introducido seguira insertando comentarios al mismo usuario
		WHILE (@maxPorUsu > @contador)
		BEGIN
			set @cuando = dbo.getRandomDate('2017-01-01', '2019-10-10');
			set @contador = @contador + 1;
			insert into comentario (usuId,numcom,comenta,cuando) values (@usuId,@contador,@comenta,@cuando)
		
		END
	FETCH NEXT FROM cursorComentario INTO @usuId
	END
	--cerrar cursor
	CLOSE cursorComentario
	--destruir cursor
	DEALLOCATE cursorComentario

GO
--EXEC gen_comenta 10,6
--select * from comentario;
--delete from comentario;


--comentarios que son respuesta
--comentarios a los que se responde
--Lo vamos a hacer sobre el 30% de los comentarios:
declare @c int;
select @c=count(*)*.30 from comentario;

--Ejecuta lo siguiente; suponemos que ese 30% es, en realidad, 15 comentarios.
select top(@c) usuId,numcom from comentario order by newid()

--Obtienes 15 comentarios seleccionados aleatoriamente. Para lo que queremos hacer necesitamos numerar las filas. Reutilizamos la expresión anterior como subconsulta:
select usuId,numcom,row_number() over (order by usuId) rn
from (select top(15) usuId,numcom from comentario order by newid()) t

--Si ejecutamos 2 veces obtendríamos 2 listas de comentarios ordenados por un entero secuencial:

--select t1.usuId,t1.numcom,t2.usuId ra,t2.numcom ran from t1, t2 
--where t1.rn=t2.rn and (t1.usuId<>t2.usuId or t1.numcom<>t2.numcom); 


--UPDATE comentario set respondea=t2.usuId, respondeanum=t2.numcom 
--from comentario c 
--	--comentario que es respuesta
--join  t1 on (c.usuId=t1.usuId and c.numcom=t1.numcom)
----comentario al que responde 
--join  t2 on (t1.rn=t2.rn) 
--where t1.usuId<>t2.usuId or t1.numcom<>t2.numcom;--que no sean el mismo


--Crear valoraciones
CREATE PROCEDURE gen_valoraciones @numUsu int, @numComentario int

AS

declare @numUsu int, @numComentario int, @estrellas int , @usuId int, @valoradoPor int, @numCom int

set @numComentario = 10;
set @numUsu = 3;

--Un 25% de los comentarios estarán valorados; el 5% de los usuarios hace esas valoraciones. 
--seleccionar @nusu usuarios
--seleccionar @ncom comentarios a ser valorados
--insertar en VALORA @nusu*@ncom/2 filas del producto cartesiano de las dos consultas anteriores.

--Obtenemos el numero de estrellas de forma aleatoria entre 1 y 5
set @estrellas =floor(rand()*5)+1;

insert into valora (usuId,valorado,numcom,estrellas) values (@usuId,@valoradoPor,@numCom,@estrellas)
select * from comentario

select * from valora
DELETE FROM valora

--Asignar palabras clave

--Se pide asociar a cada comentario entre 0 y 4 palabras clave elegidas de entre la lista almacenada en PALABRASCLAVE. 
--Esta asociación se guarda, a su vez, en la tabla COMKEYW. El máximo de palabras clave será parámetro del PA.


--La formas más inmediata de conseguirlo es con un cursor sobre los identificadores de comentario y, para cada fila, 
--insertar en COMKEYW el resultado de una consulta que combine el identificador del comentario (usuId,numcom) con los primeros resultados 
--(entre 0 y 4) de un ordenación aleatoria de las palabras clave.

CREATE PROCEDURE asig_palabras_clave @maximoPalabrasClave int

AS
--Declarar variables
DECLARE  @cuantos int,@usuId int, @numCom int,@numeroPalabrasClave int,@clave varchar(50),@contador int;

--Declarar cursor sobre comentario @numComentarios dentro de la tabla comentarios
DECLARE cursorComentario CURSOR FOR select numcom,usuId from comentario;
--Abrir cursor
OPEN cursorComentario
--leer primera fila del cursor (@usuid,@numcom)
	FETCH NEXT FROM cursorComentario INTO @usuId,@numCom
	WHILE @@FETCH_STATUS = 0
	BEGIN
	
	--Se generaran el siguiente numero de palabras clave entre 0 y 4 para cada comentario
	set @numeroPalabrasClave =floor(rand()*@maximoPalabrasClave);
	set @contador = 0

	while (@numeroPalabrasClave > @contador)
	begin
	--Tenemos que coger aleatoriamente el @maximoPalabrasClave de la tabla palabras clave y guardarlos junto con el usuId y el numComentario en la tabla comkeyw
	--insertar (rand()*(@maxpals+1) filas en comkeyw (@usuid,@numcom,keyw) desde palabrasclave
		select @clave = keyw from palabrasclave order by NEWID();
		IF (select count(*) from comkeyw where keyw = @clave) = 0 	
		begin	
		insert into comkeyw (keyw,usuId,numcom) values (@clave,@usuId,@numCom)
		set @contador = @contador + 1;
		end
		
	end

	FETCH NEXT FROM cursorComentario INTO @usuId,@numCom
	END
	--cerrar cursor
	CLOSE cursorComentario
	--destruir cursor
	DEALLOCATE cursorComentario

	GO

	exec asig_palabras_clave 4

	--select * from comkeyw order by usuId,numcom
	--delete from comkeyw
	--select * from comentario


--Crear conexiones

CREATE PROCEDURE crearConexiones 

AS

--Declarar variables
declare @hoy datetime, @numeroConexiones int, @contador int, @usuId int;

--Declarar cursor sobre comentario @numComentarios dentro de la tabla comentarios
DECLARE cursorUsuario CURSOR FOR select usuId from usuario;;
--Abrir cursor
OPEN cursorUsuario
--leer primera fila del cursor (@usuid,@numcom)
	FETCH NEXT FROM cursorUsuario INTO @usuId
	WHILE @@FETCH_STATUS = 0
	BEGIN

	--Se generaran el siguiente numero de conexiones entre 1 y 3 para cada usuario
	set @numeroConexiones =floor(rand()*3+1);
	set @contador = 0
	while (@numeroConexiones > @contador)
	BEGIN

	set @hoy=dbo.getRandomDate('20150101','20190930');
	insert into conexion (usuId,entra) values (@usuId,@hoy)
	set @contador = @contador + 1;

	END

	FETCH NEXT FROM cursorUsuario INTO @usuId
	END
	--cerrar cursor
	CLOSE cursorUsuario
	--destruir cursor
	DEALLOCATE cursorUsuario

GO

	--exec crearConexiones
	--select * from conexion
	--delete from conexion

--Combinar todo en un único PA

CREATE PROCEDURE generar_bd @numusus int

AS
exec generar_usuarios @numusus;
exec modificarPerfiles;
exec gen_sigue
exec gen_comenta 10,5
--exec gen_valoraciones 3, 10
exec asig_palabras_clave 3
exec crearConexiones 


--select 
--	(select count(*) from usuario) usuarios,
--	(select count(*) from sigue) sigue,
--	(select count(*) from comentario) comentarios,
--	(select count(*) from comentario where respondea is not null) respuestas,
--	(select count(*) from valora) valoraciones,
--	(select count(*) from comkeyw) palabras_clave,
--    (select count(*) from conexion) conexiones;
