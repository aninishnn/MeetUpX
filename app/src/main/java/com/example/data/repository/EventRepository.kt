package com.example.data.repository

import com.example.data.model.Event

// აპლიკაციის საწყის ღონისძიებების სიას ინახავს
class EventRepository {
    fun getRealisticEvents(): List<Event> {
        return listOf(
            Event(
                id = "event_tech_01",
                title = "Tbilisi DevFest & Hackathon",
                description = "Join hundreds of local developers, designers, and tech innovators at Tech Park Georgia! Collaborate on building cutting-edge Android apps using Jetpack Compose. Free snacks, drinks, and exciting tech prizes.",
                category = "Tech Meetup",
                date = "July 12, 2026",
                time = "10:00 AM - 6:00 PM",
                location = "Tech Park Georgia, Tbilisi",
                imageName = "img_onboarding"
            ),
            Event(
                id = "event_wine_01",
                title = "Vera Courtyard Boutique Wine Tasting",
                description = "Discover the 8,000-year-old Georgian winemaking heritage in the cozy streets of historic Vera. Sample organic Saperavi, Kisi, and Rkatsiteli amber wines fermented in traditional clay Qvevris, paired with premium local cheeses.",
                category = "Wine Tasting",
                date = "July 18, 2026",
                time = "6:00 PM - 9:00 PM",
                location = "Vera Wine Cellar, Tbilisi",
                imageName = "img_event_food"
            ),
            Event(
                id = "event_music_01",
                title = "Tbilisi Open Air Music Festival",
                description = "Get ready for the biggest music event in the Caucasus! Experience three days of outstanding rock, indie, electronic, and pop performances by legendary international headliners and popular local Georgian bands at Lisi Wonderland.",
                category = "Music Festival",
                date = "July 24, 2026",
                time = "2:00 PM - 2:00 AM",
                location = "Lisi Wonderland, Tbilisi",
                imageName = "img_event_music"
            ),
            Event(
                id = "event_hike_01",
                title = "Gergeti Trinity Alpine Hiking Trip",
                description = "Breathe in the pristine mountain air! Embark on a panoramic trek to the iconic 14th-century Gergeti Trinity Church, resting under the majestic snow-capped peak of Mount Kazbek. End the day with handmade hot mountain Khinkali.",
                category = "Hiking Trip",
                date = "July 15, 2026",
                time = "8:00 AM - 4:00 PM",
                location = "Kazbegi Mountains, Stepantsminda",
                imageName = "img_event_sport"
            ),
            Event(
                id = "event_tech_02",
                title = "Kutaisi Innovation Hub Tech Meetup",
                description = "Connect with the Imereti tech scene! Gather with local founders, students, and digital creators to discuss emerging technologies, cross-platform mobile development, and remote working opportunities in Kutaisi.",
                category = "Tech Meetup",
                date = "August 02, 2026",
                time = "4:00 PM - 7:00 PM",
                location = "Akaki Tsereteli University Hub, Kutaisi",
                imageName = "img_onboarding"
            ),
            Event(
                id = "event_wine_02",
                title = "Imeretian Amber Wine Masterclass",
                description = "Taste the vibrant, high-acid clay-aged wines of western Georgia. Our expert sommelier in Kutaisi will guide you through rare local varietals (Tsolikouri, Tsitska, Krakhuna) served alongside regional culinary delights.",
                category = "Wine Tasting",
                date = "September 12, 2026",
                time = "7:00 PM - 10:00 PM",
                location = "Mon Plaisir Garden, Kutaisi",
                imageName = "img_event_food"
            ),
            Event(
                id = "event_music_02",
                title = "Black Sea Jazz & Soul Festival",
                description = "Enjoy soulful, groove-filled performances by international and Georgian jazz stars. Batumi's premier summer festival combines ocean breezes, sandy beaches, and exceptional live music for unforgettable nights.",
                category = "Music Festival",
                date = "July 26, 2026",
                time = "8:00 PM - midnight",
                location = "Batumi Tennis Club, Batumi",
                imageName = "img_event_music"
            ),
            Event(
                id = "event_hike_02",
                title = "Borjomi Coniferous Forest Trek",
                description = "Explore the majestic temperate forests of the Borjomi-Kharagauli National Park. Hike along winding streams, scenic pine ridge lines, and natural mineral hot springs. A refreshing, eco-friendly wilderness escape.",
                category = "Hiking Trip",
                date = "August 14, 2026",
                time = "9:00 AM - 5:00 PM",
                location = "National Park Visitor Center, Borjomi",
                imageName = "img_event_sport"
            ),
            Event(
                id = "event_tech_03",
                title = "Batumi Beach DevCon & Sunset Networking",
                description = "Where tech meets coastal relaxation! Join developers, project managers, and digital nomads on the Black Sea coast for lightning talks about modern cloud systems, Kotlin Coroutines, and responsive UI, followed by fireside beers.",
                category = "Tech Meetup",
                date = "August 20, 2026",
                time = "5:00 PM - 9:00 PM",
                location = "Batumi Tech Space, Batumi Boulevard",
                imageName = "img_onboarding"
            ),
            Event(
                id = "event_wine_03",
                title = "Signagi Autumn Rtveli Grape Harvest",
                description = "Experience Rtveli—the historic Georgian grape harvest! Travel to Kakheti's city of love, help harvest organic Saperavi grapes, press them in traditional wooden troughs, and feast on a grand supra with live folk songs.",
                category = "Wine Tasting",
                date = "September 25, 2026",
                time = "10:00 AM - 6:00 PM",
                location = "Cradle of Wine Cellar, Signagi",
                imageName = "img_event_food"
            ),
            Event(
                id = "event_music_03",
                title = "Batumi Beach Electro Sunset Festival",
                description = "Dance to melodic deep house and ambient techno as the sun melts into the stunning Black Sea horizon. Featuring multiple stages, tropical beach bars, bonfire gatherings, and local visual art exhibitions.",
                category = "Music Festival",
                date = "August 22, 2026",
                time = "6:00 PM - 2:00 AM",
                location = "Miracle Square Beach, Batumi",
                imageName = "img_event_music"
            ),
            Event(
                id = "event_hike_03",
                title = "Mestia Alpine Hike & Svan Tower Exploration",
                description = "Embark on an epic high-altitude mountain hike to the Chalaadi Glacier, framed by the mighty peaks of Svaneti. Explore medieval Svan stone defense towers and enjoy delicious hearty Kubdari meat pies afterwards.",
                category = "Hiking Trip",
                date = "August 18, 2026",
                time = "9:00 AM - 3:00 PM",
                location = "Chalaadi Glacier Trail, Mestia",
                imageName = "img_event_sport"
            )
        )
    }
}
