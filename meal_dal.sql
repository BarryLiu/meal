USE [master]
GO
/****** Object:  Database [meal_supplement]    Script Date: 2016/8/2 21:07:42 ******/
CREATE DATABASE [meal_supplement]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'meal_supplement', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\meal_supplement.mdf' , SIZE = 29696KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'meal_supplement_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\meal_supplement_log.ldf' , SIZE = 2048KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [meal_supplement] SET COMPATIBILITY_LEVEL = 110
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [meal_supplement].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [meal_supplement] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [meal_supplement] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [meal_supplement] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [meal_supplement] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [meal_supplement] SET ARITHABORT OFF 
GO
ALTER DATABASE [meal_supplement] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [meal_supplement] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [meal_supplement] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [meal_supplement] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [meal_supplement] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [meal_supplement] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [meal_supplement] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [meal_supplement] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [meal_supplement] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [meal_supplement] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [meal_supplement] SET  DISABLE_BROKER 
GO
ALTER DATABASE [meal_supplement] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [meal_supplement] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [meal_supplement] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [meal_supplement] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [meal_supplement] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [meal_supplement] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [meal_supplement] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [meal_supplement] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [meal_supplement] SET  MULTI_USER 
GO
ALTER DATABASE [meal_supplement] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [meal_supplement] SET DB_CHAINING OFF 
GO
ALTER DATABASE [meal_supplement] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [meal_supplement] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
USE [meal_supplement]
GO
/****** Object:  Table [dbo].[t_bill]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_bill](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[bill_name] [nvarchar](30) NULL,
	[start_time] [datetime] NULL,
	[end_time] [datetime] NULL,
	[start_time_str] [nvarchar](30) NULL,
	[end_time_str] [nvarchar](30) NULL,
	[total_hours] [int] NULL,
	[name] [nvarchar](30) NULL,
	[depart] [nvarchar](100) NULL,
	[number] [nvarchar](30) NULL,
	[descs] [nvarchar](300) NULL,
	[bill_type] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_bill_type]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_bill_type](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[bill_name] [nvarchar](30) NOT NULL,
	[bill_type] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[bill_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_fees]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_fees](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_info] [nvarchar](100) NULL,
	[name] [nvarchar](30) NULL,
	[number] [nvarchar](30) NULL,
	[day_str] [nvarchar](100) NULL,
	[start_time] [nvarchar](100) NULL,
	[end_time] [nvarchar](100) NULL,
	[status] [nvarchar](100) NULL,
	[workovertime] [int] NULL,
	[fee1] [int] NULL,
	[fee2] [int] NULL,
	[fee3] [int] NULL,
	[fee4] [int] NULL,
	[fee5] [int] NULL,
	[fee_type] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_group]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_group](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[group_name] [nvarchar](30) NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_group_permission]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_group_permission](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[groupid] [bigint] NOT NULL,
	[permissionid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_kaoqin]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_kaoqin](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[nos] [nvarchar](100) NULL,
	[cardno] [nvarchar](100) NULL,
	[userid] [nvarchar](100) NULL,
	[names] [nvarchar](100) NULL,
	[depart] [nvarchar](100) NULL,
	[dotimes] [nvarchar](100) NULL,
	[addresss] [nvarchar](200) NULL,
	[status] [nvarchar](100) NULL,
	[descs] [nvarchar](500) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_leave]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_leave](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[bill_id] [bigint] NULL,
	[bill_name] [nvarchar](30) NULL,
	[user_info] [nvarchar](30) NULL,
	[name] [nvarchar](30) NULL,
	[number] [nvarchar](30) NULL,
	[day_str] [nvarchar](30) NULL,
	[start_time] [nvarchar](30) NULL,
	[end_time] [nvarchar](30) NULL,
	[total_hours] [int] NULL,
	[stat] [nvarchar](30) NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_permission]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_permission](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[per_name] [nvarchar](30) NOT NULL,
	[per_value] [int] NOT NULL,
	[parent_value] [int] NULL,
	[per_type] [int] NULL,
	[url] [nvarchar](100) NULL,
	[per_desc] [nvarchar](100) NULL,
	[isexpand] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[per_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[per_value] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_user]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_user](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[login_count] [nvarchar](100) NULL,
	[login_pwd] [nvarchar](100) NULL,
	[name] [nvarchar](30) NULL,
	[display_name] [nvarchar](30) NULL,
	[depart] [nvarchar](100) NULL,
	[number] [nvarchar](30) NULL,
	[stat] [int] NULL,
	[last_login_time] [datetime] NULL,
	[user_type] [int] NULL,
	[enter_date] [nvarchar](30) NULL,
	[email] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_user_group]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_user_group](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[userid] [bigint] NULL,
	[groupid] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[t_user_permission]    Script Date: 2016/8/2 21:07:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[t_user_permission](
	[_id] [bigint] IDENTITY(1,1) NOT NULL,
	[userid] [bigint] NOT NULL,
	[permissionid] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
USE [master]
GO
ALTER DATABASE [meal_supplement] SET  READ_WRITE 
GO





SET IDENTITY_INSERT [dbo].[t_group_permission] OFF
SET IDENTITY_INSERT [dbo].[t_permission] ON 

INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (1, N'用户管理', 100100, -1, 1, N'#', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (2, N'用户', 100101, 100100, 2, N'user', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (3, N'权限', 100102, 100100, 2, N'permission', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (4, N'我的菜单', 100200, -1, 1, N'#', NULL, 1)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (5, N'我的考勤', 100201, 100200, 2, N'kaoqin', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (6, N'我的餐补', 100202, 100200, 2, N'mySupplement', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (7, N'我的考勤异常', 100203, 100200, 2, N'myleave', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (8, N'我的统计', 100204, 100200, 2, N'mystatistic', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (9, N'统计查询', 100300, -1, 1, N'#', NULL, 1)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (10, N'所有餐补', 100301, 100300, 2, N'search', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (11, N'所有考勤异常', 100302, 100300, 2, N'leave', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (12, N'所有统计', 100303, 100300, 2, N'statistic', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (13, N'后台管理', 100400, -1, 1, N'#', NULL, 1)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (14, N'上传考勤记录', 100401, 100400, 2, N'upload.jsp', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (15, N'考勤异常表单', 100402, 100400, 2, N'bill', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (16, N'系统管理', 100500, -1, 1, N'#', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (17, N'考勤异常单据类型', 100501, 100500, 2, N'billtype', NULL, 0)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (18, N'考勤榜', 100600, -1, 1, N'#', NULL, 1)
INSERT [dbo].[t_permission] ([_id], [per_name], [per_value], [parent_value], [per_type], [url], [per_desc], [isexpand]) VALUES (19, N'周末加班榜', 100601, 100600, 2, N'week', NULL, 0)
SET IDENTITY_INSERT [dbo].[t_permission] OFF
SET IDENTITY_INSERT [dbo].[t_user] ON 


GO
SET IDENTITY_INSERT [dbo].[t_bill_type] ON 

INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (1, N'年假', 2)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (2, N'病假', 3)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (3, N'丧假', 0)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (4, N'婚假', 2)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (5, N'产假（陪产假）', 2)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (6, N'事假', 2)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (7, N'调休', 0)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (8, N'未打卡', 3)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (9, N'公出', 3)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (10, N'出差', 1)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (11, N'调休（非正常）', 0)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (12, N'其他假期', 1)
INSERT [dbo].[t_bill_type] ([_id], [bill_name], [bill_type]) VALUES (13, N'交通故障', 3)
SET IDENTITY_INSERT [dbo].[t_bill_type] OFF
SET IDENTITY_INSERT [dbo].[t_group] ON 





