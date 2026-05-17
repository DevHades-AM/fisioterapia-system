-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 17-05-2026 a las 18:59:49
-- Versión del servidor: 5.7.24
-- Versión de PHP: 7.2.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `registro_pacientes`
--
CREATE DATABASE IF NOT EXISTS `registro_pacientes` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `registro_pacientes`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asistencias`
--

CREATE TABLE `asistencias` (
  `id` int(11) NOT NULL,
  `ci_paciente` int(18) NOT NULL,
  `fecha` date DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `catalogo_tratamientos`
--

CREATE TABLE `catalogo_tratamientos` (
  `id_tratamiento` int(11) NOT NULL,
  `nombre_tratamiento` varchar(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paciente`
--

CREATE TABLE `paciente` (
  `nombre` varchar(50) DEFAULT NULL,
  `apellido` varchar(50) DEFAULT NULL,
  `fecha` date DEFAULT NULL,
  `ocupacion` varchar(50) DEFAULT NULL,
  `ci` int(18) NOT NULL,
  `patologia` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paciente_tratamiento`
--

CREATE TABLE `paciente_tratamiento` (
  `ci_paciente` int(18) NOT NULL,
  `id_tratamiento` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `asistencias`
--
ALTER TABLE `asistencias`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_ci_fecha` (`ci_paciente`,`fecha`);

--
-- Indices de la tabla `catalogo_tratamientos`
--
ALTER TABLE `catalogo_tratamientos`
  ADD PRIMARY KEY (`id_tratamiento`),
  ADD UNIQUE KEY `nombre_tratamiento` (`nombre_tratamiento`);

--
-- Indices de la tabla `paciente`
--
ALTER TABLE `paciente`
  ADD PRIMARY KEY (`ci`);

--
-- Indices de la tabla `paciente_tratamiento`
--
ALTER TABLE `paciente_tratamiento`
  ADD PRIMARY KEY (`ci_paciente`,`id_tratamiento`),
  ADD KEY `id_tratamiento` (`id_tratamiento`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `asistencias`
--
ALTER TABLE `asistencias`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `asistencias`
--
ALTER TABLE `asistencias`
  ADD CONSTRAINT `asistencias_ibfk_1` FOREIGN KEY (`ci_paciente`) REFERENCES `paciente` (`ci`);

--
-- Filtros para la tabla `paciente_tratamiento`
--
ALTER TABLE `paciente_tratamiento`
  ADD CONSTRAINT `paciente_tratamiento_ibfk_1` FOREIGN KEY (`ci_paciente`) REFERENCES `paciente` (`ci`) ON DELETE CASCADE,
  ADD CONSTRAINT `paciente_tratamiento_ibfk_2` FOREIGN KEY (`id_tratamiento`) REFERENCES `catalogo_tratamientos` (`id_tratamiento`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
