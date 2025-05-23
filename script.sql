USE [master]
GO
/****** Object:  Database [OnlineCourses]    Script Date: 4/9/2025 7:17:29 PM ******/
CREATE DATABASE [OnlineCourses]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'OnlineCourses', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.MSSQLSERVER\MSSQL\DATA\OnlineCourses.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'OnlineCourses_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.MSSQLSERVER\MSSQL\DATA\OnlineCourses_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [OnlineCourses] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [OnlineCourses].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [OnlineCourses] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [OnlineCourses] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [OnlineCourses] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [OnlineCourses] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [OnlineCourses] SET ARITHABORT OFF 
GO
ALTER DATABASE [OnlineCourses] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [OnlineCourses] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [OnlineCourses] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [OnlineCourses] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [OnlineCourses] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [OnlineCourses] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [OnlineCourses] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [OnlineCourses] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [OnlineCourses] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [OnlineCourses] SET  DISABLE_BROKER 
GO
ALTER DATABASE [OnlineCourses] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [OnlineCourses] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [OnlineCourses] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [OnlineCourses] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [OnlineCourses] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [OnlineCourses] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [OnlineCourses] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [OnlineCourses] SET RECOVERY FULL 
GO
ALTER DATABASE [OnlineCourses] SET  MULTI_USER 
GO
ALTER DATABASE [OnlineCourses] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [OnlineCourses] SET DB_CHAINING OFF 
GO
ALTER DATABASE [OnlineCourses] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [OnlineCourses] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [OnlineCourses] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [OnlineCourses] SET QUERY_STORE = OFF
GO
USE [OnlineCourses]
GO
/****** Object:  Table [dbo].[categories]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[categories](
	[category_id] [uniqueidentifier] NOT NULL,
	[created_at] [datetime2](6) NULL,
	[description] [nvarchar](max) NULL,
	[name] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[category_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[course_category]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[course_category](
	[category_id] [bigint] NOT NULL,
	[course_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[category_id] ASC,
	[course_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[courses]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[courses](
	[course_id] [uniqueidentifier] NOT NULL,
	[created_at] [datetime2](6) NULL,
	[description] [varchar](255) NULL,
	[price] [numeric](38, 2) NULL,
	[thumbnail_url] [varchar](255) NULL,
	[title] [varchar](255) NOT NULL,
	[updated_at] [datetime2](6) NULL,
	[instructor_id] [uniqueidentifier] NULL,
	[rating] [float] NULL,
PRIMARY KEY CLUSTERED 
(
	[course_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Enrollments]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Enrollments](
	[enrollment_id] [uniqueidentifier] NOT NULL,
	[user_id] [uniqueidentifier] NOT NULL,
	[course_id] [uniqueidentifier] NOT NULL,
	[enrolled_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[enrollment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[user_id] ASC,
	[course_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[lessons]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[lessons](
	[lesson_id] [uniqueidentifier] NOT NULL,
	[order_number] [int] NOT NULL,
	[title] [varchar](255) NOT NULL,
	[video_url] [varchar](255) NOT NULL,
	[course_id] [uniqueidentifier] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[lesson_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[purchases]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[purchases](
	[purchase_id] [uniqueidentifier] NOT NULL,
	[amount] [numeric](38, 2) NULL,
	[created_at] [datetime2](6) NULL,
	[payment_method] [varchar](255) NULL,
	[status] [varchar](255) NULL,
	[transaction_id] [varchar](255) NULL,
	[course_id] [uniqueidentifier] NOT NULL,
	[user_id] [uniqueidentifier] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[purchase_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[users]    Script Date: 4/9/2025 7:17:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[user_id] [uniqueidentifier] NOT NULL,
	[created_at] [datetime2](6) NULL,
	[email] [varchar](255) NOT NULL,
	[full_name] [varchar](100) NULL,
	[password] [varchar](255) NOT NULL,
	[role] [varchar](50) NULL,
	[updated_at] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK6dotkott2kjsp8vw4d0m25fb7] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[courses] ADD  CONSTRAINT [DF_Courses_Rating]  DEFAULT ((0.0)) FOR [rating]
GO
ALTER TABLE [dbo].[Enrollments] ADD  DEFAULT (newid()) FOR [enrollment_id]
GO
ALTER TABLE [dbo].[Enrollments] ADD  DEFAULT (getdate()) FOR [enrolled_at]
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD  CONSTRAINT [fk_enrollment_course] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Enrollments] CHECK CONSTRAINT [fk_enrollment_course]
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD  CONSTRAINT [fk_enrollment_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Enrollments] CHECK CONSTRAINT [fk_enrollment_users]
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD  CONSTRAINT [FK3hjx6rcnbmfw368sxigrpfpx0] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[Enrollments] CHECK CONSTRAINT [FK3hjx6rcnbmfw368sxigrpfpx0]
GO
ALTER TABLE [dbo].[Enrollments]  WITH CHECK ADD  CONSTRAINT [FKho8mcicp4196ebpltdn9wl6co] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
GO
ALTER TABLE [dbo].[Enrollments] CHECK CONSTRAINT [FKho8mcicp4196ebpltdn9wl6co]
GO
ALTER TABLE [dbo].[lessons]  WITH CHECK ADD  CONSTRAINT [fk_lesson_course] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[lessons] CHECK CONSTRAINT [fk_lesson_course]
GO
ALTER TABLE [dbo].[lessons]  WITH CHECK ADD  CONSTRAINT [FK17ucc7gjfjddsyi0gvstkqeat] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
GO
ALTER TABLE [dbo].[lessons] CHECK CONSTRAINT [FK17ucc7gjfjddsyi0gvstkqeat]
GO
ALTER TABLE [dbo].[purchases]  WITH CHECK ADD  CONSTRAINT [fk_purchase_course] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[purchases] CHECK CONSTRAINT [fk_purchase_course]
GO
ALTER TABLE [dbo].[purchases]  WITH CHECK ADD  CONSTRAINT [fk_purchase_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([user_id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[purchases] CHECK CONSTRAINT [fk_purchase_users]
GO
ALTER TABLE [dbo].[purchases]  WITH CHECK ADD  CONSTRAINT [FKrm7vf7p2lbfroh3hbpsbfg0mn] FOREIGN KEY([course_id])
REFERENCES [dbo].[courses] ([course_id])
GO
ALTER TABLE [dbo].[purchases] CHECK CONSTRAINT [FKrm7vf7p2lbfroh3hbpsbfg0mn]
GO
USE [master]
GO
ALTER DATABASE [OnlineCourses] SET  READ_WRITE 
GO
