import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import {useEffect, useState} from "react";
import {useSalgsoppgaveJob} from "../providers/SalgsoppgaveJobProvider";

import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import icon from './location-pin.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';

// Set up the default icon for markers
const DefaultIcon = L.icon({
    iconUrl: icon,
    shadowUrl: iconShadow,
    iconSize: [41, 41],       // size of the icon
    iconAnchor: [20.5, 41],     // point of the icon which corresponds to marker's location
    popupAnchor: [1, -34],    // point from which the popup should open relative to the iconAnchor
    shadowSize: [41, 41],     // size of the shadow
    shadowAnchor: [12, 41]    // point of the shadow which corresponds to marker's location
});

L.Marker.prototype.options.icon = DefaultIcon;


interface MapComponentProps {
    zoom: number;
}

const MapComponent: React.FC<MapComponentProps> = ({ zoom }) => {
    const {salgsoppgaveJob} = useSalgsoppgaveJob()

    const [center, setCenter] = useState<[number, number] | null>(null);

    useEffect(() => {
        if (salgsoppgaveJob?.salgsoppgave?.latitude && salgsoppgaveJob?.salgsoppgave?.longtitude) {
            const latitude = salgsoppgaveJob.salgsoppgave.latitude;
            const longitude = salgsoppgaveJob.salgsoppgave.longtitude;
            setCenter([latitude, longitude]);
        }
    }, [salgsoppgaveJob]);

    return (
        <>
            {center ? (
                <div className="w-full h-full z-0">
                    <MapContainer
                        // @ts-ignore
                        center={center}
                        zoom={zoom}
                        scrollWheelZoom={false}
                        className="h-full w-full rounded-lg"
                    >
                        <TileLayer
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            // @ts-ignore
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        />
                        <Marker position={center}>
                            <Popup>
                                {salgsoppgaveJob?.salgsoppgave?.gateNavn} {salgsoppgaveJob?.salgsoppgave?.gateNummer}, {salgsoppgaveJob?.salgsoppgave?.postNummer} {salgsoppgaveJob?.salgsoppgave?.postSted}
                            </Popup>
                        </Marker>
                    </MapContainer>
                </div>
            ) : (
                <div>
                    Klarte ikke laste inn kart
                </div>
            )}
        </>
    );
};

export default MapComponent;