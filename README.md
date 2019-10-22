# BeaconScanner

Realtime scanner для AltBeacon меток.

Запрашивает и проверяет permission к ACCESS_FINE_LOCATION, доступность Bluetooth модуля и его включение.

Отображает видимые в реальном времени AltBeacon метки с помощью recyclerView, ранжируя список по убыванию мощности сигнала.


Используется:

LiveData

ViewModel

android-beacon-library


![Alt text](/scr/device-2019-10-22-131616.png?raw=true "Bluetooth enabling request")
![Alt text](/scr/device-2019-10-22-131627.png?raw=true "Location request")
![Alt text](/scr/device-2019-10-22-131641.png?raw=true "Beacon list")
