# Azores


### Usage

- GET /frequency
- POST /frequency

    ```
    {
        type: "TONE",
        frequency: 111
    }

    {
        type: "AM",
        carrierFrequency: 111,
        modulatorFrequency: 3
    }

    {
        type: "FM",
        carrierFrequency: 111,
        modulatorFrequency: 3
    }
  
    {
        type: "TONE",
        frequency: 111,
        zone: "America/Denver",
        from: "10:00",
        to: "22:00"
    }
    ```

### About

Starts a specified tone, AM frequency, or FM frequency.

### Resonance

"The base length dimension of the Great Pyramid informs the fundamental resonant tone created by the
structure. Each base side has been roughly calculated at 765’, creating a fundamental frequency of 1.45
Hz when the pyramids are stimulated into high amplitude."[[1]]

It is believed the Great Pyramid of Giza was powered by a ram pump from the Nile River. As the mineral composition of the granite contains a high crystalline content, the intense vibrations from the pump caused the æther to ring like a bell at a specific frequency.[[2]]

[1]:http://www.human-resonance.org/A1_Psychoacoustics_&_Earth_Resonance.pdf

[2]:https://www.newdawnmagazine.com/articles/a-new-theory-for-the-great-pyramid-how-science-is-changing-our-view-of-the-past
