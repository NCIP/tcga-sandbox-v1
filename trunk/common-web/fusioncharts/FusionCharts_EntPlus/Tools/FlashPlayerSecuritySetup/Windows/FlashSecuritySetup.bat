@echo off
REM *** This batch file will add current folder to Flash Player Trust Zone.
REM *** Copyright Infosoft Global (P) Ltd.
set FlashFolder=%appdata%\Macromedia\Flash Player\#Security\FlashPlayerTrust
md "%FlashFolder%" >nul
echo %cd% >> "%FlashFolder%\FusionCharts.cfg"
